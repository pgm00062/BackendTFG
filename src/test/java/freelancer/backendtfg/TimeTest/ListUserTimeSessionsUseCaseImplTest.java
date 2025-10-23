package freelancer.backendtfg.TimeTest;

import freelancer.backendtfg.application.useCaseImpl.timeUseCaseImpl.ListUserTimeSessionsUseCaseImpl;
import freelancer.backendtfg.domain.mapper.TimeMapper;
import freelancer.backendtfg.infrastructure.controller.dto.output.timesOutput.TimeSessionOutputDto;
import freelancer.backendtfg.infrastructure.repository.entity.TimeEntity;
import freelancer.backendtfg.infrastructure.repository.entity.UserEntity;
import freelancer.backendtfg.infrastructure.repository.port.TimeRepositoryPort;
import freelancer.backendtfg.infrastructure.repository.port.UserRepositoryPort;
import freelancer.backendtfg.shared.exceptions.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests Unitarios para ListUserTimeSessionsUseCaseImpl
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitarios - Listar Sesiones de Tiempo")
class ListUserTimeSessionsUseCaseImplTest {

    @Mock
    private TimeRepositoryPort timeRepository;

    @Mock
    private UserRepositoryPort userRepository;

    @Mock
    private TimeMapper timeMapper;

    @InjectMocks
    private ListUserTimeSessionsUseCaseImpl listUserTimeSessionsUseCase;

    private String userEmail;
    private UserEntity userEntity;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        userEmail = "pablo@test.com";
        pageable = PageRequest.of(0, 10);

        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setEmail(userEmail);
    }

    @Test
    @DisplayName("Debería listar sesiones paginadas del usuario")
    void deberiaListarSesionesPaginadas() {
        // ARRANGE
        TimeEntity time1 = new TimeEntity();
        TimeEntity time2 = new TimeEntity();
        Page<TimeEntity> timePage = new PageImpl<>(Arrays.asList(time1, time2));

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(timeRepository.findByUserEmail(anyString(), any(Pageable.class))).thenReturn(timePage);
        when(timeMapper.toOutputDto(any(TimeEntity.class))).thenReturn(new TimeSessionOutputDto());

        // ACT
        Page<TimeSessionOutputDto> result = listUserTimeSessionsUseCase.listTimeSessions(userEmail, pageable);

        // ASSERT
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        verify(timeRepository, times(1)).findByUserEmail(userEmail, pageable);
    }

    @Test
    @DisplayName("Debería lanzar UserNotFoundException cuando el usuario no existe")
    void deberiaLanzarExcepcionCuandoUsuarioNoExiste() {
        // ARRANGE
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(UserNotFoundException.class,
            () -> listUserTimeSessionsUseCase.listTimeSessions(userEmail, pageable));

        verify(timeRepository, never()).findByUserEmail(anyString(), any());
    }

    @Test
    @DisplayName("Debería retornar página vacía si el usuario no tiene sesiones")
    void deberiaRetornarPaginaVaciaSiNoHaySesiones() {
        // ARRANGE
        Page<TimeEntity> emptyPage = new PageImpl<>(Arrays.asList());

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(userEntity));
        when(timeRepository.findByUserEmail(anyString(), any())).thenReturn(emptyPage);

        // ACT
        Page<TimeSessionOutputDto> result = listUserTimeSessionsUseCase.listTimeSessions(userEmail, pageable);

        // ASSERT
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.isEmpty());
    }
}
