package su.mandora.tarasande.mixin.accessor;

public interface IGameRenderer {

    void setAllowThroughWalls(boolean allowThroughWalls);
    boolean isAllowThroughWalls();

    void setDisableReachExtension(boolean disableReachExtension);
    boolean isDisableReachExtension();

    void setReach(double reach);
    double getReach();

}
