package chat.network.dtos;

import chat.model.ChallengeDTO;
import chat.model.ChildDTO;

import java.io.Serializable;
import java.util.List;

public class UpdateDTO implements Serializable {
    private ChildDTO childDTO;
    private List<ChallengeDTO> challenges;

    public UpdateDTO(ChildDTO childDTO, List<ChallengeDTO> challenges) {
        this.childDTO = childDTO;
        this.challenges = challenges;
    }

    public ChildDTO getChildDTO() {
        return childDTO;
    }

    public void setChildDTO(ChildDTO childDTO) {
        this.childDTO = childDTO;
    }

    public List<ChallengeDTO> getChallenges() {
        return challenges;
    }

    public void setChallenges(List<ChallengeDTO> challenges) {
        this.challenges = challenges;
    }
}
