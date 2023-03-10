package com.dmdev.service;

import com.dmdev.dao.SubscriptionDao;
import com.dmdev.dto.CreateSubscriptionDto;
import com.dmdev.entity.Provider;
import com.dmdev.entity.Status;
import com.dmdev.entity.Subscription;
import com.dmdev.integration.IntegrationTestBase;
import com.dmdev.mapper.CreateSubscriptionMapper;
import com.dmdev.validator.CreateSubscriptionValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SubscriptionServiceIT extends IntegrationTestBase {

	private SubscriptionService subscriptionService;
	private SubscriptionDao subscriptionDao;

	@BeforeEach
	void init() {
		subscriptionDao = SubscriptionDao.getInstance();
		subscriptionService = new SubscriptionService(
				subscriptionDao,
				CreateSubscriptionMapper.getInstance(),
				CreateSubscriptionValidator.getInstance(),
				Clock.fixed(Instant.now(), ZoneOffset.UTC)
		);
	}

	@Test
	void upsertNewSubscription() {
		CreateSubscriptionDto createSubscriptionDto = getSubscriptionDto();

		Subscription actualResult = subscriptionService.upsert(createSubscriptionDto);

		assertNotNull(actualResult.getId());
	}

	@Test
	void upsertExistingSubscription() {
		Subscription subscription = subscriptionDao.insert(getSubscription());
		CreateSubscriptionDto createSubscriptionDto = getSubscriptionDto();

		Subscription actualResult = subscriptionService.upsert(createSubscriptionDto);

		Subscription updatedSubscription = subscriptionDao.findById(actualResult.getId()).get();
		assertThat(updatedSubscription.getId()).isEqualTo(subscription.getId());
	}

	@Test
	void cancel() {
		Subscription subscription = subscriptionDao.insert(getSubscription());

		subscriptionService.cancel(subscription.getId());

		Subscription updatedSubscription = subscriptionDao.findById(subscription.getId()).get();
		assertThat(updatedSubscription.getStatus()).isEqualTo(Status.CANCELED);
	}

	@Test
	void expire() {
		Subscription subscription = subscriptionDao.insert(getSubscription());

		subscriptionService.expire(subscription.getId());

		Subscription updatedSubscription = subscriptionDao.findById(subscription.getId()).get();
		assertThat(updatedSubscription.getStatus()).isEqualTo(Status.EXPIRED);
	}

	private static Subscription getSubscription() {
		return Subscription.builder()
				.id(1)
				.userId(1)
				.name("Subscription")
				.provider(Provider.GOOGLE)
				.expirationDate(getInstant())
				.status(Status.ACTIVE)
				.build();
	}

	private static CreateSubscriptionDto getSubscriptionDto() {
		return CreateSubscriptionDto.builder()
				.userId(1)
				.name("Subscription")
				.provider("GOOGLE")
				.expirationDate(getInstant())
				.build();
	}

	private static Instant getInstant() {
		LocalDate localDate = LocalDate.parse("2023-02-28");
		LocalDateTime localDateTime = localDate.atStartOfDay();
		return localDateTime.toInstant(ZoneOffset.UTC);
	}
}
