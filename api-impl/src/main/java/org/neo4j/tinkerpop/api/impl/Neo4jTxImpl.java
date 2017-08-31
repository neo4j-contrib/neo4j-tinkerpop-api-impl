/**
 * Copyright (C) 2015 Neo Technology
 * <p/>
 * This file is part of neo4j-tinkerpop-binding <http://neo4j.com>.
 * <p/>
 * structr is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p/>
 * structr is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License
 * along with neo4j-tinkerpop-binding.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.tinkerpop.api.impl;

import org.neo4j.graphdb.Transaction;
import org.neo4j.tinkerpop.api.Neo4jTx;

/**
 * @author mh
 * @since 25.03.15
 */
public class Neo4jTxImpl implements Neo4jTx {
    private final Transaction tx;

    Neo4jTxImpl(Transaction tx) {
        this.tx = tx;
    }

    @Override
    public void failure() {
        tx.failure();
    }

    @Override
    public void success() {
        tx.success();
    }

    @Override
    public void close() {
        tx.close();
    }

    @Override
    public String toString() {
        return this.tx.toString();
    }
}
