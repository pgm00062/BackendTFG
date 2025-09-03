package freelancer.backendtfg.domain.mapper;

import freelancer.backendtfg.infrastructure.controller.dto.input.projectsInput.ProjectCreateInputDto;
import freelancer.backendtfg.infrastructure.controller.dto.output.projectsOutput.ProjectOutputDto;
import freelancer.backendtfg.infrastructure.controller.dto.output.projectsOutput.ProjectOutputDtoParcial;
import freelancer.backendtfg.infrastructure.repository.entity.ProjectEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "status", source = "status")
    ProjectEntity toEntity(ProjectCreateInputDto inputDto);

    @Mapping(target = "status", source = "status")
    ProjectOutputDto toOutputDto(ProjectEntity entity);

    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "status", source = "status")
    ProjectOutputDtoParcial toOutputDtoParcial(ProjectEntity entity);
}
