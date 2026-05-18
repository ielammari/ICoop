package ch.epfl.cs107.icoop.handler;

import ch.epfl.cs107.icoop.ICoopBehavior;
import ch.epfl.cs107.icoop.actor.Door;
import ch.epfl.cs107.icoop.actor.Explosive;
import ch.epfl.cs107.icoop.actor.ICoopPlayer;
import ch.epfl.cs107.icoop.actor.Obstacle;
import ch.epfl.cs107.icoop.actor.Rock;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;

public interface ICoopInteractionVisitor extends AreaInteractionVisitor {
    default void interactWith(ICoopBehavior.ICoopCell cell, boolean isCellInteraction) {}
    default void interactWith(ICoopPlayer player, boolean isCellInteraction) {}
    default void interactWith(Door door, boolean isCellInteraction) {}
    default void interactWith(Obstacle obstacle, boolean isCellInteraction) {}
    default void interactWith(Rock rock, boolean isCellInteraction) {}
    default void interactWith(Explosive explosive, boolean isCellInteraction) {}
}
