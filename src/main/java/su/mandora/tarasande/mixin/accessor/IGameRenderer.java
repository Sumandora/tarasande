package su.mandora.tarasande.mixin.accessor;

public interface IGameRenderer {

    void setAllowThroughWalls(boolean allowThroughWalls);
    boolean isAllowThroughWalls();
    void setReach(double reach);
    double getReach();

}
