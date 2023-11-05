/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.

James G Willmore - LJ Computing - (C) 2023
*/
package net.ljcomputing.glue.service.impl;

import jakarta.transaction.Transactional;
import java.util.List;
import net.ljcomputing.glue.entity.Trash;
import net.ljcomputing.glue.repository.gluepg.PgTrashRepository;
import net.ljcomputing.glue.service.TrashService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("pgTrashService")
@Transactional
public class PgTrashServiceImpl implements TrashService {
    private final PgTrashRepository trashRepository;

    public PgTrashServiceImpl(@Autowired final PgTrashRepository trashRepository) {
        this.trashRepository = trashRepository;
    }

    @Override
    public Trash save(final Trash entity) {
        return trashRepository.save(entity);
    }

    @Override
    public List<Trash> findAll() {
        return trashRepository.findAll();
    }

    @Override
    public List<Trash> findByValue(final String value) {
        return trashRepository.findByValue(value);
    }

    @Override
    public void delete(final Long id) {
        trashRepository.deleteById(id);
    }

    @Override
    public void delete(final Trash entity) {
        trashRepository.delete(entity);
    }
}
