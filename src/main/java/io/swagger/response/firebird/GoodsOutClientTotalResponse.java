package io.swagger.response.firebird;

import io.swagger.firebird.model.DocumentOut;
import io.swagger.firebird.model.GoodsOutClient;
import io.swagger.firebird.model.ServiceGoodsAddon;
import lombok.Data;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class GoodsOutClientTotalResponse {

    private List<GoodsOutClientResponse> goodsOutClientResponseList;

    public GoodsOutClientTotalResponse(DocumentOut documentOut) {

        if ( documentOut == null ) return;

        Set<GoodsOutClient> goodsOutClients = documentOut.getGoodsOutClients();

        if ( goodsOutClients == null || goodsOutClients.size() == 0 ) return;

        goodsOutClientResponseList = goodsOutClients.stream().map(GoodsOutClientResponse::new).collect( Collectors.toList() );

    }

}
