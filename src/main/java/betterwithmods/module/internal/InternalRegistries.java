package betterwithmods.module.internal;

import betterwithmods.module.RequiredModule;

public class InternalRegistries extends RequiredModule {


    @Override
    public void setup() {
        addFeatures(
                new UnitTesting(),
                new BlockRegistry(),
                new SoundRegistry(),
                new EntityRegistry()
        );
    }

    @Override
    public int priority() {
        return Integer.MAX_VALUE;
    }
}
