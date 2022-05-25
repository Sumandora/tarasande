package su.mandora.tarasande.mixin.accessor;

public interface IGameRenderer {

    boolean isAllowThroughWalls();

    void setAllowThroughWalls(boolean allowThroughWalls);

    boolean isDisableReachExtension();

    void setDisableReachExtension(boolean disableReachExtension);

    double getReach();

    void setReach(double reach);

}
