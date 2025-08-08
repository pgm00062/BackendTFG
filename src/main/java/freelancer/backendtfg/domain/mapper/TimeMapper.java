package freelancer.backendtfg.domain.mapper;

import freelancer.backendtfg.infrastructure.controller.dto.input.timesInput.TimeStartInputDto;
import freelancer.backendtfg.infrastructure.controller.dto.output.timesOutput.TimeSessionOutputDto;
import freelancer.backendtfg.infrastructure.repository.entity.TimeEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TimeMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "startTime", ignore = true)
    @Mapping(target = "endTime", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "project", ignore = true)
    TimeEntity toEntity(TimeStartInputDto inputDto);

    @Mapping(target = "projectId", source = "project.id")
    @Mapping(target = "projectName", source = "project.name")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userName", expression = "java(entity.getUser().getName() + \" \" + entity.getUser().getSurname())")
    @Mapping(target = "duration", expression = "java(entity.getDuration())")
    @Mapping(target = "durationInMinutes", expression = "java(entity.getDurationInMinutes())")
    @Mapping(target = "durationInHours", expression = "java(entity.getDurationInHours())")
    @Mapping(target = "formattedDuration", expression = "java(entity.getDuration().toHours() > 0 ? String.format(\"%dh %dm\", entity.getDuration().toHours(), entity.getDuration().toMinutes() % 60) : String.format(\"%dm\", entity.getDuration().toMinutes()))")
    TimeSessionOutputDto toOutputDto(TimeEntity entity);
} 