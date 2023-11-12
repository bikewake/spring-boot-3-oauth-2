package org.bikewake.chat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.MonoProcessor;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class MessagingApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	public void simulation() {
		Flux<Long> theFluxForSSE = Flux.interval(Duration.ofMillis(100));

		MonoProcessor<String> processor = MonoProcessor.create();
		Executors.newSingleThreadScheduledExecutor().schedule(() -> processor.onNext("STOP"), 2, TimeUnit.SECONDS);

		theFluxForSSE.takeUntilOther(processor.log())
				.log()
				.blockLast();
	}

}
