package freelancer.backendtfg.domain.mapper;

import freelancer.backendtfg.infrastructure.controller.dto.input.usersInput.UserRegisterInputDto;
import freelancer.backendtfg.infrastructure.controller.dto.output.usersOutput.UserLoginOutputDto;
import freelancer.backendtfg.infrastructure.controller.dto.output.usersOutput.UserProfileOutputDto;
import freelancer.backendtfg.infrastructure.controller.dto.output.usersOutput.UserRegisterOutputDto;
import freelancer.backendtfg.infrastructure.repository.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper (componentModel = "spring")
public interface UserMapper {

    // De DTO a entidad JPA
    @Mapping(target = "id", ignore = true) // ID será generado por la base de datos
    UserEntity toEntity(UserRegisterInputDto inputDto);

    // De entidad a DTO de salida
    UserRegisterOutputDto toOutputDto(UserEntity entity);

    //para el login
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "email", source = "email")
    UserLoginOutputDto toLoginOutputDto(UserEntity user);

    //para el register
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "email", source = "email")
    UserRegisterOutputDto toRegisterOutputDto(UserEntity entity);

    // Para el perfil (sin token)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "surname", source = "surname")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "dni", source = "dni")
    UserProfileOutputDto toProfileOutputDto(UserEntity user);

}