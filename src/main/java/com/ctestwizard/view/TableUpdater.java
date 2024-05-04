package com.ctestwizard.view;

import com.ctestwizard.model.testentity.TInterface;

@FunctionalInterface
public interface TableUpdater {
    void updateStubCode(TInterface tInterface);
}
