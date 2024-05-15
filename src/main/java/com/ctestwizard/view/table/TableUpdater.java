package com.ctestwizard.view.table;

import com.ctestwizard.model.test.entity.TInterface;

@FunctionalInterface
public interface TableUpdater {
    void updateStubCode(TInterface tInterface);
}
