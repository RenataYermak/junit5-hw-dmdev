package com.dmdev.mapper;

import com.dmdev.dto.CreateSubscriptionDto;
import com.dmdev.entity.Provider;
import com.dmdev.entity.Status;
import com.dmdev.entity.Subscription;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

class CreateSubscriptionMapperTest {

	private final CreateSubscriptionMapper mapper = CreateSubscriptionMapper.getInstance();

	@Test
	void map() {
		CreateSubscriptionDto dto = CreateSubscriptionDto.builder()
				.userId(1)
				.name("Subscription")
				.provider(Provider.GOOGLE.name())
				.expirationDate(getInstant())
				.build();

		Subscription actualResult = mapper.map(dto);

		Subscription expectedResult = Subscription.builder()
				.userId(1)
				.name("Subscription")
				.provider(Provider.GOOGLE)
				.expirationDate(getInstant())
				.status(Status.ACTIVE)
				.build();
		Assertions.assertThat(actualResult).isEqualTo(expectedResult);
	}

	private static Instant getInstant() {
		LocalDate localDate = LocalDate.parse("2023-02-28");
		LocalDateTime localDateTime = localDate.atStartOfDay();
		return localDateTime.toInstant(ZoneOffset.UTC);
	}
}
