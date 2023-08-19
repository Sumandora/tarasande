package su.mandora.tarasande.injection.accessor;

public interface IGameRenderer {

    boolean tarasande_isAllowThroughWalls();
    void tarasande_setAllowThroughWalls(boolean allowThroughWalls);

    boolean tarasande_isDisableReachExtension();
    void tarasande_setDisableReachExtension(boolean disableReachExtension);

    double tarasande_getReach();
    void tarasande_setReach(double reach);

    double tarasande_getBlockReach();
    void tarasande_setBlockReach(double reach);

    boolean tarasande_isSelfInflicted();
    void tarasande_setSelfInflicted(boolean selfInflicted);

}
