/**
 * Copyright (C) 2015 Neo Technology
 *
 * This file is part of neo4j-tinkerpop-binding <http://neo4j.com>.
 *
 * Neo Technology licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
