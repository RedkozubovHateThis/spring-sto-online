package io.swagger.response.firebird;

import io.swagger.firebird.model.GoodsOutClient;
import io.swagger.firebird.model.ServiceGoodsAddon;
import lombok.Data;

@Data
public class GoodsOutClientResponse {

    private Integer id;
    private String number;
    private String fullName;
    private Integer goodsCount;

    public GoodsOutClientResponse(GoodsOutClient goodsOutClient) {

        if ( goodsOutClient == null ) return;

        id = goodsOutClient.getId();
        number = goodsOutClient.getNumber();
        fullName = goodsOutClient.getName();
        goodsCount = goodsOutClient.getGoodsCount();

    }

}
