package com.techpulse.mapper;

import com.techpulse.dto.ProjectRequestDTO;
import com.techpulse.dto.ProjectResponseDTO;
import com.techpulse.entity.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    @Mapping(target = "projectId", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "employees", ignore = true)
    Project toEntity(ProjectRequestDTO dto);

    @Mapping(target = "companyId", expression = "java(project.getCompany() != null ? project.getCompany().getCompanyId() : null)")
    @Mapping(target = "employeeIds", ignore = true)
    ProjectResponseDTO toDTO(Project project);
}

