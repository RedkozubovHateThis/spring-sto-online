package io.swagger.postgres.model.enums;

public enum SubscriptionType {
    FREE("Пробный", true, 0.0, 0.0, 5, 15),
    ECONOMIC("Эконом", false, 7500.0, 250.0, 30, 15),
    STANDARD("Стандарт", false, 11500.0, 230.0, 50, 15),
    PROF("Проф", false, 16000.0, 200.0, 80, 15);

    private final String name;
    private final Boolean isFree;
    private final Double cost;
    private final Double documentCost;
    private final Integer documentsCount;
    private final Integer durationDays;

    SubscriptionType(String name, Boolean isFree, Double cost, Double documentCost, Integer documentsCount,
                             Integer durationDays) {
        this.name = name;
        this.isFree = isFree;
        this.cost = cost;
        this.documentCost = documentCost;
        this.documentsCount = documentsCount;
        this.durationDays = durationDays;
    }

    public String getName() {
        return name;
    }

    public Boolean getFree() {
        return isFree;
    }

    public Double getCost() {
        return cost;
    }

    public Double getDocumentCost() {
        return documentCost;
    }

    public Integer getDocumentsCount() {
        return documentsCount;
    }

    public Integer getDurationDays() {
        return durationDays;
    }
}
