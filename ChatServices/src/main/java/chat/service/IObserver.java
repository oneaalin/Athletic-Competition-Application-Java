package chat.service;

import chat.model.ChallengeDTO;
import chat.model.ChildDTO;

import java.util.List;

public interface IObserver {
    void updateChildren(ChildDTO childDTO,List<ChallengeDTO> challenges);
}
