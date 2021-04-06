package chat.persistence;

import chat.model.Challenge;

public interface IChallengeRepository extends IRepository<Long, Challenge> {
    /**
     * finds a challenge by some properties
     * @param minimumAge the minimum age of the challenge
     * @param maximumAge the maximum age of the challenge
     * @return Challenge
     */
    Challenge findByProperties(int minimumAge,int maximumAge,String name);
}
