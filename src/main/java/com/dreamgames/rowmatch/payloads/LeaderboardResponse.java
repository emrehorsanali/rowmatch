package com.dreamgames.rowmatch.payloads;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LeaderboardResponse {
    private Long userId;
    private String username;
    private Integer score;
    private Long groupId;
    private Long tournamentId;

    public LeaderboardResponse(UserResponse userResponse, Long groupId, Long tournamentId, Double score) {
        this.userId = userResponse.getUserId();
        this.username = userResponse.getUsername();
        this.score = score.intValue();
        this.groupId = groupId;
        this.tournamentId = tournamentId;
    }
}
