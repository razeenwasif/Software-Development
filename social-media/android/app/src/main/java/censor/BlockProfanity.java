package censor;

import java.util.Set;

/**
 * A concrete implementation of a censor that blocks the entire message if a profanity is found.
 */
class BlockProfanity extends AbstractCensor {

    public BlockProfanity(Set<String> badWords, Set<String> goodWords) {
        super(badWords, goodWords);
    }

    /**
     * {@inheritDoc}
     * @return true, indicating the block-on-profanity strategy should be used.
     */
    @Override
    protected boolean isBlockOnProfanity() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String handleProfanity(String originalWord, String profaneWord) {
        return "";
    }
}
