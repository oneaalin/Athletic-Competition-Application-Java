package chat.persistence;

import chat.model.Entry;
import chat.model.Tuple;

public interface IEntriesRepository extends IRepository<Tuple<Long,Long>, Entry> {
    /**
     * return the number of children that participates to a given challenge
     * @param cid the id of the challenge
     * @return the number of children that participates
     */
    int findChildNumber(Long cid);

    /**
     * returns the number of challenges of a child
     * @param kid the id of the child
     * @return the number of challenges of a child
     */
    int findChallengeNumber(Long kid);

}