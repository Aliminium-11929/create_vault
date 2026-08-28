package com.craete.vault.Application.ProjectMemberships.DTOs;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMembershipStorageModel {

    private UUID id;
    private UUID projectId;
    private Long memberId;

}
