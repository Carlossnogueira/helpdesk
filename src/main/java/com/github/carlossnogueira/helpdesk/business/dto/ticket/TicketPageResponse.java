package com.github.carlossnogueira.helpdesk.business.dto.ticket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter @Setter
@AllArgsConstructor
@Builder
public class TicketPageResponse {

    private int page;
    private int TotalPages;
    private int size;
    private boolean lastPage;
    private List<TicketDetailsDto> tickets;

}
