package com.ctestwizard.view.table;

import com.ctestwizard.model.test.entity.TInterface;

/**
 * Interface for updating the stub code table.
 */
@FunctionalInterface
public interface TableUpdater {
    void updateStubCode(TInterface tInterface);
}
