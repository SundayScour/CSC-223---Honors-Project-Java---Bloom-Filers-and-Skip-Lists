/*
 * Copyright 2016 Tilmann Zaeschke
 * 
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
package importedImplementations.TinSpinIndexes.index.rtree;

public interface RTreeLogic {

	<T> RTreeNode<T> chooseSubTree(RTreeNode<T> root, RTreeEntry<T> e,
			int desiredInsertionLevel, int nLevels);
	
	<T> boolean hasSpace(RTreeNode<T> root);

	<T> RTreeNode<T> split(RTreeNode<T> node, RTreeEntry<T> e);

	<T> RTreeEntry<T>[] reInsert(RTreeNode<T> node, RTreeEntry<T> e);

}
