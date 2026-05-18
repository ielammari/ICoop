package ch.epfl.cs107.icoop.handler;

import ch.epfl.cs107.icoop.actor.ICoopPlayer;

public interface ICoopAreaTransitioner {
    void transitionTo(ICoopPlayer player, String destinationArea);
}