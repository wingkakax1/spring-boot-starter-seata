/*
 * Copyright (C) 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.seata.feign;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import feign.Client;
import feign.Feign;

/**
 *
 * @author liff
 * @date 2019年8月1日
 */
@Configuration
@ConditionalOnClass(Client.class)
@AutoConfigureBefore(FeignAutoConfiguration.class)
public class SeataFeignClientAutoConfiguration {

	@Bean
	@Scope("prototype")
	@ConditionalOnClass(name = "com.netflix.hystrix.HystrixCommand")
	@ConditionalOnProperty(name = "feign.hystrix.enabled", havingValue = "true")
	Feign.Builder feignHystrixBuilder(BeanFactory beanFactory) {
		return SeataHystrixFeignBuilder.builder(beanFactory);
	}

//	@Bean
//	@Scope("prototype")
//	@ConditionalOnClass(name = "com.alibaba.csp.sentinel.SphU")
//	@ConditionalOnProperty(name = "feign.sentinel.enabled", havingValue = "true")
//	Feign.Builder feignSentinelBuilder(BeanFactory beanFactory) {
//		return FescarSentinelFeignBuilder.builder(beanFactory);
//	}

	@Bean
	@ConditionalOnMissingBean
	@Scope("prototype")
	Feign.Builder feignBuilder(BeanFactory beanFactory) {
		return SeataFeignBuilder.builder(beanFactory);
	}

	@Configuration
	protected static class FeignBeanPostProcessorConfiguration {

		@Bean
		SeataBeanPostProcessor seataBeanPostProcessor(
				SeataFeignObjectWrapper seataFeignObjectWrapper) {
			return new SeataBeanPostProcessor(seataFeignObjectWrapper);
		}

		@Bean
		SeataContextBeanPostProcessor seataContextBeanPostProcessor(
				BeanFactory beanFactory) {
			return new SeataContextBeanPostProcessor(beanFactory);
		}

		@Bean
		SeataFeignObjectWrapper seataFeignObjectWrapper(BeanFactory beanFactory) {
			return new SeataFeignObjectWrapper(beanFactory);
		}
	}

}