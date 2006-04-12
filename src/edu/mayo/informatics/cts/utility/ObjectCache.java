/*
 * Copyright: (c) 2002-2006 Mayo Foundation for Medical Education and
 * Research (MFMER).  All rights reserved.  MAYO, MAYO CLINIC, and the
 * triple-shield Mayo logo are trademarks and service marks of MFMER.
 *
 * Except as contained in the copyright notice above, the trade names, 
 * trademarks, service marks, or product names of the copyright holder shall
 * not be used in advertising, promotion or otherwise in connection with
 * this Software without prior written authorization of the copyright holder.
 * 
 * Licensed under the Eclipse Public License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 * 		http://www.eclipse.org/legal/epl-v10.html
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.mayo.informatics.cts.utility;

import org.apache.commons.collections.map.*;

/**
 * A class to aid in the caching of often used info, to save trips to the database.
 * 
 * @author <A HREF="mailto:armbrust.daniel@mayo.edu">Dan Armbrust</A>
 */
public class ObjectCache 
{
    private LRUMap values_;
    private static ObjectCache oc_ = null;

    /**
     * Create a cache of size CTSConstants.cacheSize;
     * @return
     */
    public static ObjectCache instance()
    {
        if (oc_ == null) 
        {
            oc_ = new ObjectCache();
        }
        return oc_;
    }
    
    /*
     * Singleton pattern
     */
    private ObjectCache()
    {
        //A Least Recently Used map will automaticaly discard old items if it is full, 
        //and new items are added.
        values_ = new LRUMap(CTSConstants.CACHE_SIZE.getValue());
    }

    public Object put(Object key, Object value)
    {
        return values_.put(key, value);
    }
    
    public Object get(Object key)
    {
        return values_.get(key);
    }
}