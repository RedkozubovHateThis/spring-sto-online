package io.swagger.response.info;

import lombok.Data;

@Data
public class ModeratorInfo {

    private Long servicesCount;
    private Long moderatorsCount;

    private Integer documentsRemainsAll;
    private Integer totalDocumentsAll;

    private Double balanceAll;
    private Boolean balanceValid;

    private Integer totalDraftAll;

}
