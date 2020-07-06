package io.swagger.response.api;

import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public abstract class JsonApiParamsBase<P> {

    private Map<String, List<String>> filter;
    private List<String> sort;
    private List<String> include;
    private Map<String, Integer> page;

    public abstract P getFilterPayload() throws Exception;

    public PageRequest getPageable() {
        int number;
        int size = page.getOrDefault("size", 20);

        if ( !page.containsKey("number") )
            number = 0;
        else
            number = page.get("number") - 1;

        if ( sort == null || sort.size() == 0 )
            return PageRequest.of(number, size);

        String firstField = sort.get(0);
        Sort sortDomain;

        if (firstField.startsWith("-")) {
            List<String> sortFixed = sort.stream().map( eachSort -> eachSort.replaceFirst("-", "") ).collect( Collectors.toList() );
            sortDomain = Sort.by(Sort.Direction.DESC, sortFixed.toArray( new String[ sort.size() ] ) );
        }
        else
            sortDomain = Sort.by(Sort.Direction.ASC, sort.toArray( new String[ sort.size() ] ));

        return PageRequest.of(number, size, sortDomain);
    }
}
