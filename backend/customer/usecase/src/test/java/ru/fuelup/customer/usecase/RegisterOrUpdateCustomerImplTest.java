package ru.fuelup.customer.usecase;

import io.vavr.control.Either;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.fuelup.common.platform.Platform;
import ru.fuelup.customer.domain.Customer;
import ru.fuelup.customer.domain.OnboardingStatus;
import ru.fuelup.customer.usecase.impl.RegisterOrUpdateCustomerImpl;
import ru.fuelup.customer.usecase.port.CustomerRepository;
import ru.fuelup.customer.usecase.port.InitDataValidator;
import ru.fuelup.customer.usecase.port.JwtTokenService;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterOrUpdateCustomerImplTest {

    @Mock CustomerRepository customerRepository;
    @Mock InitDataValidator initDataValidator;
    @Mock JwtTokenService jwtTokenService;

    @InjectMocks RegisterOrUpdateCustomerImpl sut;

    private InitDataValidator.UserData validUserData;

    @BeforeEach
    void setUp() {
        validUserData = new InitDataValidator.UserData(123456789L, "Иван", "Петров", "ivan_p");
        when(jwtTokenService.generate(any(), anyString(), any())).thenReturn("jwt-token");
    }

    @Test
    void newUser_shouldRegisterAndReturnToken() {
        when(initDataValidator.validate(anyString(), eq(Platform.TELEGRAM)))
                .thenReturn(Either.right(validUserData));
        when(customerRepository.findByMessengerUserId(validUserData.getUserId(), Platform.TELEGRAM))
                .thenReturn(Optional.empty());

        var newCustomer = Customer.newFromMessenger(
                validUserData.getUserId(), Platform.TELEGRAM,
                validUserData.getFirstName(), validUserData.getLastName(), validUserData.getUsername()
        );
        newCustomer.setId(UUID.randomUUID());
        when(customerRepository.save(any())).thenReturn(newCustomer);

        var result = sut.invoke("valid-init-data", Platform.TELEGRAM);

        assertThat(result.isRight()).isTrue();
        assertThat(result.get().getJwtToken()).isEqualTo("jwt-token");
        verify(customerRepository).save(any());
    }

    @Test
    void existingUser_shouldUpdateInfoAndReturnToken() {
        var existingCustomer = Customer.newFromMessenger(
                validUserData.getUserId(), Platform.TELEGRAM, "Старое Имя", null, null
        );
        existingCustomer.setId(UUID.randomUUID());
        existingCustomer.setOnboardingStatus(OnboardingStatus.COMPLETED);

        when(initDataValidator.validate(anyString(), eq(Platform.TELEGRAM)))
                .thenReturn(Either.right(validUserData));
        when(customerRepository.findByMessengerUserId(validUserData.getUserId(), Platform.TELEGRAM))
                .thenReturn(Optional.of(existingCustomer));
        when(customerRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var result = sut.invoke("valid-init-data", Platform.TELEGRAM);

        assertThat(result.isRight()).isTrue();
        // имя должно обновиться
        verify(customerRepository).save(argThat(c -> "Иван".equals(c.getFirstName())));
    }

    @Test
    void invalidInitData_shouldReturnError() {
        when(initDataValidator.validate(anyString(), any()))
                .thenReturn(Either.left(new InitDataValidator.ValidationError.SignatureInvalid()));

        var result = sut.invoke("bad-init-data", Platform.MAX);

        assertThat(result.isLeft()).isTrue();
        assertThat(result.getLeft()).isInstanceOf(RegisterOrUpdateCustomer.RegisterError.InvalidInitDataError.class);
        verify(customerRepository, never()).save(any());
    }
}
