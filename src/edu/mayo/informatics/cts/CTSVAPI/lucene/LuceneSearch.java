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
package edu.mayo.informatics.cts.CTSVAPI.lucene;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import org.apache.commons.codec.language.DoubleMetaphone;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.hl7.CTSVAPI.BadlyFormedMatchText;
import org.hl7.CTSVAPI.ConceptDesignation;
import org.hl7.CTSVAPI.ConceptId;
import org.hl7.CTSVAPI.ConceptProperty;
import org.hl7.CTSVAPI.TimeoutError;
import org.hl7.CTSVAPI.UnexpectedError;
import org.hl7.CTSVAPI.UnknownCodeSystem;
import org.hl7.CTSVAPI.UnknownMatchAlgorithm;

import edu.mayo.informatics.cts.utility.CTSConstants;
import edu.mayo.informatics.indexer.api.IndexerService;
import edu.mayo.informatics.indexer.api.SearchServiceInterface;
import edu.mayo.informatics.indexer.api.exceptions.InternalErrorException;
import edu.mayo.informatics.indexer.api.exceptions.InternalIndexerErrorException;
import edu.mayo.informatics.indexer.api.generators.QueryGenerator;
import edu.mayo.informatics.indexer.lucene.IDFNeutralSimilarity;
import edu.mayo.informatics.indexer.lucene.analyzers.EncoderAnalyzer;
import edu.mayo.informatics.indexer.lucene.analyzers.FieldSkippingAnalyzer;
import edu.mayo.informatics.indexer.lucene.analyzers.SnowballAnalyzer;
import edu.mayo.informatics.indexer.lucene.analyzers.WhiteSpaceLowerCaseAnalyzer;

/**
 * This class implements certain search operations from VAPI with Lucene.
 * 
 * @author <A HREF="mailto:armbrust.daniel@mayo.edu">Dan Armbrust</A>
 */
public class LuceneSearch
{
    private IndexerService     service_;
    private Hashtable          indexSearchers_;
    private Hashtable          codeSystemToIndexMap_;
    private QueryParser        parser_;
    private Set                extraWhiteSpaceChars_;
    private String[]           enabledCodeSystems_;

    public final static Logger logger = Logger.getLogger("edu.mayo.informatics.cts.VAPI_LUCENE");

    /**
     * Public method to create a LuceneSearch object. If the parameters are missing, they are read from the CTSConstants
     * class (which is populated by the properties file)
     * 
     * @return
     * @throws UnexpectedError
     */
    public LuceneSearch(String indexLocation, String[] enableForCodeSystems) throws UnexpectedError
    {
        enabledCodeSystems_ = enableForCodeSystems;
        try
        {
            if (indexLocation == null || indexLocation.length() == 0)
            {
                indexLocation = CTSConstants.LUCENE_INDEX_LOCATION.getValue();
            }

            init(indexLocation);
        }
        catch (UnexpectedError e)
        {
            throw e;
        }
        catch (Exception e)
        {
            logger.error("Lucene initialization error", e);
            throw new UnexpectedError("There was an error initializing the Lucene Searcher - " + e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString()));
        }
    }

    private void init(String indexLocation) throws UnexpectedError
    {
        try 
        {
			service_ = new IndexerService(indexLocation, false);
			indexSearchers_ = new Hashtable();
			codeSystemToIndexMap_ = new Hashtable();

			if (CTSConstants.LUCENE_SEARCH_ENABLED.getValue())
			{
			    WhiteSpaceLowerCaseAnalyzer wslca = new WhiteSpaceLowerCaseAnalyzer(new String[]{},
			            WhiteSpaceLowerCaseAnalyzer.getDefaultCharRemovalSet(), WhiteSpaceLowerCaseAnalyzer
			                    .getDefaultWhiteSpaceSet());

			    extraWhiteSpaceChars_ = wslca.getCurrentCharRemovalTable();

			    // Use a FieldSkippingAnalyzer, so it doesn't tokenize on the non-tokenized fields.
			    PerFieldAnalyzerWrapper analyzer = new PerFieldAnalyzerWrapper(new FieldSkippingAnalyzer(new String[]{
			            "codingSchemeName", "conceptCode", "isActive", "isPreferred", "presentationFormat", "language",
			            "conceptStatus", "propertyId", "dataType", "degreeOfFidelity", "representationalForm",
			            "matchIfNoContext", "property"}, wslca));

			    if (CTSConstants.LUCENE_DOUBLE_METAPHONE_SEARCH_ENABLED.getValue())
			    {
			        EncoderAnalyzer temp = new EncoderAnalyzer(new DoubleMetaphone(), new String[]{},
			                WhiteSpaceLowerCaseAnalyzer.getDefaultCharRemovalSet(), WhiteSpaceLowerCaseAnalyzer
			                        .getDefaultWhiteSpaceSet());
			        analyzer.addAnalyzer("dm_propertyValue", temp);
			    }
                
                if (CTSConstants.LUCENE_STEMMED_SEARCH_ENABLED.getValue())
                {
                    SnowballAnalyzer sa = new SnowballAnalyzer(false, "English", new String[]{},
                            WhiteSpaceLowerCaseAnalyzer.getDefaultCharRemovalSet(), WhiteSpaceLowerCaseAnalyzer.getDefaultWhiteSpaceSet());
                    analyzer.addAnalyzer("stem_propertyValue", sa);
                }

// LVG Norm searching has been retired.
// if (CTSConstants.LUCENE_NORM_SEARCH_ENABLED.getValue())
//			    {
//			        try
//			        {
//			            NormAnalyzer temp = new NormAnalyzer(false, new String[]{}, WhiteSpaceLowerCaseAnalyzer
//			                    .getDefaultCharRemovalSet(), WhiteSpaceLowerCaseAnalyzer.getDefaultWhiteSpaceSet());
//			            // indexerService_.createIndex(normIndexName_, temp);
//			            analyzer.addAnalyzer("norm_propertyValue", temp);
//			        }
//			        catch (NoClassDefFoundError e)
//			        {
//			            // norm is not available
//			            CTSConstants.LUCENE_NORM_SEARCH_ENABLED.setValue(false);
//			            logger.error("LuceneNormSearch could not be initialized.  Is Norm (lvg) on the classpath?", e);
//			        }
//			    }

			    parser_ = new QueryParser("propertyValue", analyzer);

			}
			else
			{
			    logger.error("Tried to init a Lucene searcher when lucene search is not enabled");
			    throw new UnexpectedError("Lucene search functionality is not enabled in the configuration files.");
			}
		} 
        catch (UnexpectedError e) 
        {
			throw e;	
        } 
        catch (InternalErrorException e) 
        {
			throw new UnexpectedError("There was a problem opening the lucene index.");
        }
    }

    private SearchServiceInterface getMultiSearcher() throws UnexpectedError
    {
        // open and cached already?
        SearchServiceInterface si = (SearchServiceInterface) indexSearchers_.get("*");

        if (si == null)
        {
            // Index names cached?
            String[] indexNames = (String[]) codeSystemToIndexMap_.get("*");

            if (indexNames == null)
            {
                // not cached. We want to create an index searcher that will search every terminology
                // that is available in the DB we are connected too...

                int emptyCount = 0;
                indexNames = new String[enabledCodeSystems_.length];
                for (int i = 0; i < enabledCodeSystems_.length; i++)
                {
                    indexNames[i] = getIndexNameForCodeSystem(enabledCodeSystems_[i]);

                    if (indexNames[i] == null || indexNames[i].length() == 0)
                    {
                        if (CTSConstants.LUCENE_THROW_ERROR_ON_MISSING_INDEX.getValue())
                        {
                            throw new UnexpectedError("The index for the code system "
                                    + enabledCodeSystems_[i]
                                    + " is not available.");
                        }
                        else
                        {
                            emptyCount++;
                            logger.warn("The index for the code system "
                                    + enabledCodeSystems_[i]
                                    + " is not available.");
                        }
                    }
                }

                // remove blanks
                String[] temp = new String[indexNames.length - emptyCount];
                int j = 0;
                for (int i = 0; i < temp.length; i++)
                {
                    String next = indexNames[j++];
                    while (next == null || next.length() == 0)
                    {
                        next = indexNames[j++];
                    }
                    temp[i] = next;
                }
                indexNames = temp;

                // put it in the cache.
                codeSystemToIndexMap_.put("*", indexNames);
            }
            try
            {
                // only use a multisearcher if necessary, because its faster to not use it.
                if (indexNames.length == 1)
                {
                    // is it already cached?
                    si = (SearchServiceInterface) indexSearchers_.get(indexNames[0]);

                    if (si == null)
                    {
                        // not cached, create and put it in the "single" cache.
                        si = service_.getIndexSearcher(indexNames[0]);
                        indexSearchers_.put(indexNames[0], si);
                    }
                }
                else
                {
                    si = service_.getIndexSearcher(indexNames, false);
                }
                // cache it for the multi search.
                indexSearchers_.put("*", si);
            }
            catch (Exception e)
            {
                logger.error("error getting index for code system '*'", e);
                throw new UnexpectedError("Their was an error getting the index for code system '*'.");
            }
        }
        return si;
    }
    
    private String getIndexNameForCodeSystem(String codeSystemName) throws UnexpectedError
    {
        try
        {
            String[] keys = service_.getMetaData().getIndexMetaDataKeys();
            String indexName = null;
            //The indexer now puts version numbers into these keys - but I don't know them in cts.
            //so chop them off.
            for (int i = 0; i < keys.length; i++)
            {
                String temp = keys[i];
                int pos = temp.indexOf("[:]");
                if (pos != -1)
                {
                    temp = temp.substring(0, pos);
                }
                if (temp.equals(codeSystemName))
                {
                    indexName = (String) service_.getMetaData().getIndexMetaDataValue(keys[i]);
                    break;
                }
            }
            return indexName;
        }
        catch (InternalErrorException e)
        {
            throw new UnexpectedError("Problem reading the index name for the code system: " + e);
        }
    }

    private SearchServiceInterface getSearcher(String codeSystemName, String matchAlgorithm_code)
            throws UnknownMatchAlgorithm, UnexpectedError
    {
        if (codeSystemName.equals("*"))
        {
            return getMultiSearcher();
        }

        String indexName = (String) codeSystemToIndexMap_.get(codeSystemName);

        if (indexName == null)
        {
            indexName = getIndexNameForCodeSystem(codeSystemName);

            if (indexName == null || indexName.length() == 0)
            {
                throw new UnexpectedError("The index for the code system " + codeSystemName + " is not available.");
            }
            // put it in the cache
            codeSystemToIndexMap_.put(codeSystemName, indexName);
        }

        SearchServiceInterface si = (SearchServiceInterface) indexSearchers_.get(indexName);

        if (si != null)
        {
            return si;
        }
        // if it did equal null, its not in the cache - open one up and put it in the cache.
        try
        {
            si = service_.getIndexSearcher(indexName);
            si.setSimilarity(new IDFNeutralSimilarity());
            indexSearchers_.put(indexName, si);

            return si;
        }
        catch (Exception e)
        {
            logger.error("error getting index for the match algorithm "
                    + matchAlgorithm_code
                    + " in the code system "
                    + codeSystemName, e);
            throw new UnexpectedError("The index for the match algorithm "
                    + matchAlgorithm_code
                    + " in the code system "
                    + codeSystemName
                    + " is not available.");
        }
    }

    /**
     * This method implements the CTS search lookupConceptCodesByProperties. The parameters it takes are not exactly the
     * same, nor are the exceptions that it throws. It is meant to be used in conjunction with the existing
     * implementation(s).
     * 
     * The return type is a hack - ConceptID's are suppose to contain the concept code, and code system id. In this
     * case, I put in the codeSystemName instead of ID. The ID's need to be filled in as a post process (in the existing
     * implmementation)
     * 
     */
    public ConceptId[] luceneLookupConceptCodesByProperty(String codeSystemName, String matchText,
            String matchAlgorithm_code, String language_code, boolean activeConceptsOnly, String[] properties,
            String[] mimeTypes, int timeout, int sizeLimit) throws BadlyFormedMatchText, UnexpectedError,
            UnknownMatchAlgorithm, TimeoutError
    {
        ArrayList resultsToReturn = new ArrayList();
        HashSet resultsDupeRemover = new HashSet();
        try
        {
            if (matchText == null || matchText.length() == 0)
            {
                throw new BadlyFormedMatchText("Match Text is required for this method.");
            }
            SearchServiceInterface searcher = getSearcher(codeSystemName, matchAlgorithm_code);

            StringBuffer queryString = new StringBuffer();

            queryString.append(makeMatchTextQueryPortion(matchAlgorithm_code, matchText));

            if (activeConceptsOnly)
            {
                queryString.append(" AND NOT isActive:(F)");
            }

            // if they supply *, search all code systems (by not restricting the query)
            if (!codeSystemName.equals("*"))
            {
                queryString.append(" AND codingSchemeName:(\"" + codeSystemName + "\")");
            }

            if (language_code != null && language_code.length() > 0)
            {
                queryString.append(" AND language:(" + language_code + "*)");
            }

            if (properties != null && properties.length > 0)
            {
                queryString.append(" AND property:(");
                for (int i = 0; i < properties.length; i++)
                {
                    queryString.append("\"" + properties[i] + "\"");
                    if (i + 1 < properties.length)
                    {
                        queryString.append(" OR ");
                    }
                }
                queryString.append(")");
            }

            if (mimeTypes != null && mimeTypes.length > 0)
            {
                queryString.append(" AND presentationFormat:(");
                for (int i = 0; i < mimeTypes.length; i++)
                {
                    queryString.append("\"" + mimeTypes[i] + "\"");
                    if (i + 1 < mimeTypes.length)
                    {
                        queryString.append(" OR ");
                    }
                }
                queryString.append(")");
            }

            Query query;
            try
            {
                query = parser_.parse(queryString.toString());
            }
            catch (ParseException e)
            {
                throw new BadlyFormedMatchText(matchText);
            }
            
            // make it bigger, because it will usually match on multiple designations per concept,
            // and I will end up returning less concepts than the limit requested in that case.
            int localLimit = (sizeLimit == 0 ? Integer.MAX_VALUE : sizeLimit * 5);

            Document[] docs = searcher.search(query, null, true, localLimit);
            float[] scores = searcher.getScores();
            // make it bigger, because it will usually match on multiple designations per concept,
            // and I will end up returning less concepts than the limit requested in that case.

            for (int i = 0; i < docs.length; i++)
            {
                if (sizeLimit != 0 && resultsToReturn.size() == sizeLimit)
                {
                    break;
                }
                ConceptId temp = new ConceptId();
                
                //chop off any urn:oid: prefix stuff
                String tempId = docs[i].get("codingSchemeId");
                if (tempId.toLowerCase().startsWith("urn:oid:"))
                {
                    tempId = tempId.substring("urn:oid:".length());
                }
                temp.setCodeSystem_id(tempId);
                temp.setConcept_code(docs[i].get("conceptCode"));

                if (!resultsDupeRemover.contains(temp.getCodeSystem_id() + ":" + temp.getConcept_code()))
                {
                    ScoredConceptId scoredConceptId = new ScoredConceptId();
                    scoredConceptId.conceptId = temp;
                    scoredConceptId.score = scores[i];

                    String isPreferred = docs[i].get("isPreferred");

                    scoredConceptId.isPreferred = isPreferred == null || isPreferred.equals("F") ? false
                            : true;

                    resultsToReturn.add(scoredConceptId);
                    resultsDupeRemover.add(temp.getCodeSystem_id() + ":" + temp.getConcept_code());
                }
            }
            // sort them further (break lucene ties based on preferred flags)
            Collections.sort(resultsToReturn, new ScoredConceptIdComparator());
        }
        catch (UnknownMatchAlgorithm e)
        {
            throw e;
        }
        catch (UnexpectedError e)
        {
            throw e;
        }
        catch (BadlyFormedMatchText e)
        {
            throw e;
        }
        catch (InternalIndexerErrorException e)
        {
            throw new UnexpectedError(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString()));
        }
        catch (Exception e)
        {
            logger.error("Unexpected Error", e);;
            throw new UnexpectedError(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString()));
        }

        ConceptId[] finalResult = new ConceptId[resultsToReturn.size()];
        for (int i = 0; i < resultsToReturn.size(); i++)
        {
            finalResult[i] = ((ScoredConceptId) resultsToReturn.get(i)).conceptId;
        }

        return finalResult;
    }

    /**
     * This method implements the CTS search lookupConceptCodesByDesignation. The parameters it takes are not exactly
     * the same, nor are the exceptions that it throws. It is meant to be used in conjunction with the existing
     * implementation(s).
     * 
     * The return type is a hack - ConceptID's are suppose to contain the concept code, and code system id. In this
     * case, I put in the codeSystemName instead of ID. The ID's need to be filled in as a post process (in the existing
     * implmementation)
     * 
     */
    public ConceptId[] luceneLookupConceptCodesByDesignation(String codeSystemName, String matchText,
            String matchAlgorithm_code, String language_code, boolean activeConceptsOnly, int timeout, int sizeLimit)
            throws BadlyFormedMatchText, UnexpectedError, TimeoutError, UnknownMatchAlgorithm
    {
        ArrayList resultsToReturn = new ArrayList();
        HashSet resultsDupeRemover = new HashSet();
        try
        {
            SearchServiceInterface searcher = getSearcher(codeSystemName, matchAlgorithm_code);

            StringBuffer queryString = new StringBuffer();

            queryString.append("property:(textualPresentation)");
            if (matchText != null && matchText.length() > 0)
            {
                queryString.append(" AND " + makeMatchTextQueryPortion(matchAlgorithm_code, matchText));
            }

            if (activeConceptsOnly)
            {
                queryString.append(" AND NOT isActive:(F)");
            }

            // if they supply *, search all code systems (by not restricting the query)
            if (!codeSystemName.equals("*"))
            {
                queryString.append(" AND codingSchemeName:(\"" + codeSystemName + "\")");
            }

            if (language_code != null && language_code.length() > 0)
            {
                queryString.append(" AND language:(" + language_code + "*)");
            }

            Query query;
            try
            {
                query = parser_.parse(queryString.toString());
            }
            catch (ParseException e)
            {
                throw new BadlyFormedMatchText(matchText);
            }
            
            // make it bigger, because it will usually match on multiple designations per concept,
            // and I will end up returning less concepts than the limit requested in that case.
            int localLimit = (sizeLimit == 0 ? Integer.MAX_VALUE : sizeLimit * 5);

            Document[] docs = searcher.search(query, null, true, localLimit);
            float[] scores = searcher.getScores();
            

            for (int i = 0; i < docs.length; i++)
            {
                if (sizeLimit != 0 && resultsToReturn.size() == sizeLimit)
                {
                    break;
                }
                ConceptId temp = new ConceptId();
                
                //chop off any urn:oid: prefix stuff
                String tempId = docs[i].get("codingSchemeId");
                if (tempId.toLowerCase().startsWith("urn:oid:"))
                {
                    tempId = tempId.substring("urn:oid:".length());
                }
                temp.setCodeSystem_id(tempId);
                temp.setConcept_code(docs[i].get("conceptCode"));

                if (!resultsDupeRemover.contains(temp.getCodeSystem_id() + ":" + temp.getConcept_code()))
                {
                    ScoredConceptId scoredConceptId = new ScoredConceptId();
                    scoredConceptId.conceptId = temp;
                    scoredConceptId.score = scores[i];

                    String isPreferred = docs[i].get("isPreferred");

                    scoredConceptId.isPreferred = isPreferred == null || isPreferred.equals("F") ? false
                            : true;

                    resultsToReturn.add(scoredConceptId);
                    resultsDupeRemover.add(temp.getCodeSystem_id() + ":" + temp.getConcept_code());
                }
            }
            // sort them further (break lucene ties based on preferred flags)
            Collections.sort(resultsToReturn, new ScoredConceptIdComparator());
        }
        catch (UnknownMatchAlgorithm e)
        {
            throw e;
        }
        catch (UnexpectedError e)
        {
            throw e;
        }
        catch (BadlyFormedMatchText e)
        {
            throw e;
        }
        catch (InternalIndexerErrorException e)
        {
            throw new UnexpectedError(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString()));
        }
        catch (Exception e)
        {
            logger.error("Unexpected Error", e);;
            throw new UnexpectedError(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString()));
        }

        ConceptId[] finalResult = new ConceptId[resultsToReturn.size()];

        for (int i = 0; i < resultsToReturn.size(); i++)
        {
            finalResult[i] = ((ScoredConceptId) resultsToReturn.get(i)).conceptId;
        }

        return finalResult;
    }

    /**
     * This method implements the CTS search lookupDesignations. The parameters it takes are not exactly the same, nor
     * are the exceptions that it throws. It is meant to be used in conjunction with the existing implementation(s).
     * 
     * @throws UnknownCodeSystem
     * 
     */
    public ConceptDesignation[] luceneLookupDesignations(String codeSystemName, String conceptCode, String matchText,
            String matchAlgorithm_code, String languageCode) throws BadlyFormedMatchText, UnexpectedError,
            UnknownMatchAlgorithm, UnknownCodeSystem
    {
        ArrayList resultsToReturn = new ArrayList();
        try
        {
            if (codeSystemName.equals("*"))
            {
                throw new UnknownCodeSystem("Wildcard code system is not supported on this method.");
            }

            SearchServiceInterface searcher = getSearcher(codeSystemName, matchAlgorithm_code);

            StringBuffer queryString = new StringBuffer();

            queryString.append("property:(textualPresentation)");
            if (matchText != null && matchText.length() > 0)
            {
                queryString.append(" AND " + makeMatchTextQueryPortion(matchAlgorithm_code, matchText));
            }
            queryString.append(" AND codingSchemeName:(\"" + codeSystemName + "\")");
            queryString.append(" AND conceptCode:(\"" + conceptCode + "\")");

            if (languageCode != null && languageCode.length() > 0)
            {
                queryString.append(" AND language:(" + languageCode + ")");
            }

            Query query;
            try
            {
                query = parser_.parse(queryString.toString());
            }
            catch (ParseException e)
            {
                throw new BadlyFormedMatchText(matchText);
            }

            Document[] docs = searcher.search(query, null, true, Integer.MAX_VALUE);

            for (int i = 0; i < docs.length; i++)
            {
                ConceptDesignation temp = new ConceptDesignation();
                temp.setDesignation(docs[i].get("propertyValue"));
                temp.setLanguage_code(docs[i].get("language"));
                String preferredFlag = docs[i].get("isPreferred");
                if (preferredFlag == null || preferredFlag.equals("F"))
                {
                    temp.setPreferredForLanguage(false);
                }
                else
                {
                    temp.setPreferredForLanguage(true);
                }

                resultsToReturn.add(temp);
            }

        }
        catch (UnknownMatchAlgorithm e)
        {
            throw e;
        }
        catch (UnexpectedError e)
        {
            throw e;
        }
        catch (InternalIndexerErrorException e)
        {
            throw new UnexpectedError(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString()));
        }
        return (ConceptDesignation[]) resultsToReturn.toArray(new ConceptDesignation[resultsToReturn.size()]);
    }

    public ConceptProperty[] luceneLookupProperties(String codeSystemName, String conceptCode, String[] properties,
            String matchText, String matchAlgorithm_code, String language_code, String[] mimeTypes)
            throws BadlyFormedMatchText, UnexpectedError, UnknownMatchAlgorithm, UnknownCodeSystem
    {
        //TODO - all of the Lucene query parsing in this class should be reworked in the same way 
        //that the lexbig code works, so we are only parsing the user string (not everythign else)
        ArrayList resultsToReturn = new ArrayList();
        try
        {
            if (codeSystemName.equals("*"))
            {
                throw new UnknownCodeSystem("Wildcard code system is not supported on this method.");
            }

            SearchServiceInterface searcher = getSearcher(codeSystemName, matchAlgorithm_code);

            StringBuffer queryString = new StringBuffer();
            queryString.append("codingSchemeName:(\"" + codeSystemName + "\")");
            queryString.append(" AND conceptCode:(\"" + conceptCode + "\")");

            if (matchText != null && matchText.length() > 0)
            {
                queryString.append(" AND " + makeMatchTextQueryPortion(matchAlgorithm_code, matchText));
            }

            if (language_code != null && language_code.length() > 0)
            {
                queryString.append(" AND language:(" + language_code + ")");
            }

            if (properties != null && properties.length > 0)
            {
                queryString.append(" AND property:(");
                for (int i = 0; i < properties.length; i++)
                {
                    queryString.append("\"" + properties[i] + "\"");
                    if (i + 1 < properties.length)
                    {
                        queryString.append(" OR ");
                    }
                }
                queryString.append(")");
            }

            if (mimeTypes != null && mimeTypes.length > 0)
            {
                queryString.append(" AND presentationFormat:(");
                for (int i = 0; i < mimeTypes.length; i++)
                {
                    queryString.append("\"" + mimeTypes[i] + "\"");
                    if (i + 1 < mimeTypes.length)
                    {
                        queryString.append(" OR ");
                    }
                }
                queryString.append(")");
            }

            Query query;
            try
            {
                query = parser_.parse(queryString.toString());
            }
            catch (ParseException e)
            {
                throw new BadlyFormedMatchText(matchText);
            }

            Document[] docs = searcher.search(query, null, true, Integer.MAX_VALUE);

            for (int i = 0; i < docs.length; i++)
            {
                ConceptProperty temp = new ConceptProperty();
                temp.setPropertyValue(docs[i].get("propertyValue"));
                temp.setLanguage_code(docs[i].get("language"));
                temp.setProperty_code(docs[i].get("property"));
                temp.setMimeType_code(docs[i].get("presentationFormat"));

                resultsToReturn.add(temp);
            }

        }
        catch (UnknownMatchAlgorithm e)
        {
            throw e;
        }
        catch (UnexpectedError e)
        {
            throw e;
        }
        catch (InternalIndexerErrorException e)
        {
            throw new UnexpectedError(e.toString() + " " + (e.getCause() == null ? ""
                    : e.getCause().toString()));
        }
        return (ConceptProperty[]) resultsToReturn.toArray(new ConceptProperty[resultsToReturn.size()]);

    }
    
    private String handleWhiteSpaceCharacters(String query)
    {
        return QueryGenerator.removeExtraWhiteSpaceCharacters(query, extraWhiteSpaceChars_);
    }
    
    private String makeMatchTextQueryPortion(String matchAlgoritm_code, String matchText) throws UnknownMatchAlgorithm
    {
        String modifiedMatchText = handleWhiteSpaceCharacters(matchText);
        if (matchAlgoritm_code.toLowerCase().equals("lucenequery"))
        {
            return "propertyValue:(" + modifiedMatchText + ")";
        }
        else if (matchAlgoritm_code.toLowerCase().equals("normalizedlucenequery"))
        {
            return "norm_propertyValue:(" + modifiedMatchText + ")";
        }
        else if (matchAlgoritm_code.toLowerCase().equals("doublemetaphonelucenequery"))
        {
            return "dm_propertyValue:(" + modifiedMatchText + ")";
        }
        else if (matchAlgoritm_code.toLowerCase().equals("stemmedlucenequery"))
        {
            return "stem_propertyValue:(" + modifiedMatchText + ")";
        }
        else
        {
            throw new UnknownMatchAlgorithm(matchAlgoritm_code);
        }
     }

    private class ScoredConceptId
    {
        public ConceptId conceptId;
        public float     score;
        public boolean   isPreferred;
    }

    private class ScoredConceptIdComparator implements Comparator
    {

        public int compare(Object o1, Object o2)
        {
            ScoredConceptId a = (ScoredConceptId) o1;
            ScoredConceptId b = (ScoredConceptId) o2;

            if (a.score < b.score)
            {
                return 1;
            }
            else if (a.score > b.score)
            {
                return -1;
            }
            else
            {
                if (a.isPreferred)
                {
                    return -1;
                }
                else if (b.isPreferred)
                {
                    return 1;
                }
                else
                {
                    return 0;
                }
            }

        }

    }

}