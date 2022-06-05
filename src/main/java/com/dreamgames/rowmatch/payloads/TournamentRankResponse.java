package com.dreamgames.rowmatch.payloads;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TournamentRankResponse {
    Long groupId;
    Long rank;

    public TournamentRankResponse(Long groupId, Long rank) {
        this.groupId = groupId;
        this.rank = rank + 1;
    }
}
