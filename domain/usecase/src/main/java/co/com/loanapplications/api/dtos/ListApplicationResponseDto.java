package co.com.loanapplications.api.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ListApplicationResponseDto<T> {
    private List<T> items;
    private long total;
    private int page;
    private int size;
}
