/*
 * Copyright 2010-2012 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.jet.lang.resolve.scopes.receivers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jet.lang.descriptors.ClassDescriptor;
import org.jetbrains.jet.lang.descriptors.DeclarationDescriptor;
import org.jetbrains.jet.lang.types.JetType;

/**
 * @author abreslav
 */
public class ClassReceiver implements ThisReceiverDescriptor {

    private final ClassDescriptor classDescriptor;

    public ClassReceiver(@NotNull ClassDescriptor classDescriptor) {
        this.classDescriptor = classDescriptor;
    }

    @Override
    public boolean exists() {
        return true;
    }

    @NotNull
    @Override
    public JetType getType() {
        return classDescriptor.getDefaultType();
    }

    @NotNull
    @Override
    public DeclarationDescriptor getDeclarationDescriptor() {
        return classDescriptor;
    }

    @Override
    public <R, D> R accept(@NotNull ReceiverDescriptorVisitor<R, D> visitor, D data) {
        return visitor.visitClassReceiver(this, data);
    }

    @Override
    public String toString() {
        return "Class{" + getType() + "}";
    }
}