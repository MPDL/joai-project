/*
	Copyright 2017 Digital Learning Sciences (DLS) at the
	University Corporation for Atmospheric Research (UCAR),
	P.O. Box 3000, Boulder, CO 80307

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/
package org.dlese.dpc.oai.harvester;



import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.lucene.document.Document;
import org.dlese.dpc.index.SimpleLuceneIndex;
import org.dlese.dpc.index.writer.HarvestLogWriter;



// ------------ message handler for the harvests -------------



/**

 *  A HarvestMessageHandler that writes harvest status messages to a SimpleLuceneIndex.

 *

 * @author     John Weatherley

 * @created    February 17, 2006

 */

public class IndexingHarvestMsgHandler implements HarvestMessageHandler {

	private static boolean debug = true;

	private SimpleLuceneIndex harvestLogIndex = null;

	private HarvestLogWriter harvestLogWriter = null;

	private int numRecordsForNotification;

	private Harvester harvester = null;

	private File harvestDir = null;





	/**

	 *  Constructor for the HarvestMsgHandler object

	 *

	 * @param  harvestLogIndex            The index.

	 * @param  repositoryName             The name of the repository, for indexing.

	 * @param  baseURL                    Base URL to the harvested provider.

	 * @param  set                        Set being harvested.

	 * @param  shUid                      UID of the scheduled Harvest.

	 * @param  numRecordsForNotification  Number of records the harvester should recieve

	 *      between logging progress entries.

	 * @param  hd                         Description of the Parameter

	 */

	public IndexingHarvestMsgHandler(

			SimpleLuceneIndex harvestLogIndex,

			String repositoryName,

			String baseURL,

			String set,

			String shUid,

			int numRecordsForNotification, File hd) {

		this.harvestLogIndex = harvestLogIndex;

		harvestLogWriter = new HarvestLogWriter(repositoryName, baseURL, set, shUid);

		this.numRecordsForNotification = numRecordsForNotification;

		this.harvester = harvester;

		this.harvestDir = hd;

	}





	/**

	 *  Sets the harvester attribute of the IndexingHarvestMsgHandler object

	 *

	 * @param  harvester  The new harvester value

	 */

	public void setHarvester(Harvester harvester) {

		this.harvester = harvester;

	}

	

	/**

	 *  Sets the harvest attributes for this harvest.

	 *

	 * @param  from   The from date or null if none used

	 * @param  until  The until date or null if none used

	 */	

	public void setHarvestAttributes(Date from, Date until) {

		harvestLogWriter.setHarvestAttributes(from,until);	

	}



	/**

	 *  A status message indicating an event that took place during the harvest, such as a

	 *  request made to the data provider.

	 *

	 * @param  msg  A harvest status message generated by the harvester.

	 */

	public void statusMessage(String msg) {

		prtln(msg);

		Document doc = harvestLogWriter.logEntry(

				harvester.getHarvestUid(),

				harvester.getStartTime(),

				harvester.getEndTime(),

				harvester.getNumRecordsHarvested(),

				harvester.getNumResumptionTokensIssued(),

				HarvestLogWriter.HARVEST_IN_PROGRESS,

				harvester.getHarvestedRecordsDir(),

				null,

				null,

				null,

				null,

				msg);



		Document[] addDocs = new Document[]{doc};

		harvestLogIndex.update(

				"harvestuid",

				Long.toString(harvester.getHarvestUid()),

				addDocs,

				true);

	}





	/**

	 *  A status message indicating the number of records currenlty harvested and the number

	 *  of resumption tokens issued.

	 *

	 * @param  recordCount      Number of recrods currently harvested.

	 * @param  resumptionCount  Number of resumption tokens currently issued.

	 * @see                     #getNumRecordsForStatusNotification()

	 */

	public void statusMessage(int recordCount, int resumptionCount) {

		String msg = "Harvest is progressing. (" + recordCount + " records harvested with " +

				resumptionCount + " resumption tokens issued).";

		prtln(msg);

		Document doc = harvestLogWriter.logEntry(

				harvester.getHarvestUid(),

				harvester.getStartTime(),

				harvester.getEndTime(),

				recordCount,

				resumptionCount,

				HarvestLogWriter.HARVEST_IN_PROGRESS,

				harvester.getHarvestedRecordsDir(),

				null,

				null,

				null,

				null,

				msg);



		Document[] addDocs = new Document[]{doc};

		harvestLogIndex.update(

				"harvestuid",

				Long.toString(harvester.getHarvestUid()),

				addDocs,

				true);

	}





	/**

	 *  A message generated by the harvester when a standard OAI error has occured.

	 *

	 * @param  oaiError      The OAI error code, for example "noRecordsMatch".

	 * @param  errorMessage  The accompanying message returned by the data provider, if any.

	 * @param  supportedGranularity  Supported granularity [days, seconds] or null

	 * @param  deletedRecordSupport  Deleted record support [no, transient, persistent] or null	 	 

	 */

	public void oaiErrorMessage(String oaiError, String errorMessage, String supportedGranularity, String deletedRecordSupport) {

		String msg = "OAI error " + oaiError + ". Message: " + errorMessage;

		prtln(msg);

		Document doc = harvestLogWriter.logEntry(

				harvester.getHarvestUid(),

				harvester.getStartTime(),

				harvester.getEndTime(),

				harvester.getNumRecordsHarvested(),

				harvester.getNumResumptionTokensIssued(),

				HarvestLogWriter.COMPLETED_OAI_ERROR,

				harvester.getHarvestedRecordsDir(),

				null,

				supportedGranularity,

				deletedRecordSupport,

				oaiError,

				msg);



		Document[] addDocs = new Document[]{doc};

		harvestLogIndex.update(

				"harvestuid",

				Long.toString(harvester.getHarvestUid()),

				addDocs,

				true);

	}





	/**

	 *  A serios error that occured during the harvest, preventing it from completing. For

	 *  example an http 500 error.

	 *

	 * @param  msg  Description of the error.

	 */

	public void errorMessage(String msg) {

		prtln("Error: " + msg);

		Document doc = harvestLogWriter.logEntry(

				harvester.getHarvestUid(),

				harvester.getStartTime(),

				harvester.getEndTime(),

				harvester.getNumRecordsHarvested(),

				harvester.getNumResumptionTokensIssued(),

				HarvestLogWriter.COMPLETED_SERIOUS_ERROR,

				harvester.getHarvestedRecordsDir(),

				null,

				null,

				null,

				null,

				msg);



		Document[] addDocs = new Document[]{doc};

		harvestLogIndex.update(

				"harvestuid",

				Long.toString(harvester.getHarvestUid()),

				addDocs,

				true);

	}





	/**

	 *  A final report detailing the result of a successful harvest, which only occurs if no

	 *  errors have occured.

	 *

	 * @param  recordCount      The total number of records harvested.

	 * @param  resumptionCount  Number of resumption tokens issued.

	 * @param  baseURL          The baseURL that was harvested.

	 * @param  set              The set that was harvested, or an empty string if none.

	 * @param  startTime        The time the harvest began.

	 * @param  endTime          The time the harvest was completed.

	 * @param  zipFilePathName  The full path to the harvest zip file, or null if none. 

	 * @param  supportedGranularity  Supported granularity [days, seconds]

	 * @param  deletedRecordSupport  Deleted record support [no, transient, persistent]	 

	 */

	public void completedHarvestMessage(int recordCount,

			int resumptionCount,

			String baseURL,

			String set,

			long startTime,

			long endTime,

			String zipFilePathName,

			String supportedGranularity,

	        String deletedRecordSupport) {

		if (set != null && set.length() > 0) {

			set = ", set " + set;

		}



		String msg = "Harvest of " + baseURL + set + " completed successfully. " + recordCount +

				" records were harvested. " + resumptionCount + " resumption tokens were issued.";

		prtln(msg);



		Document doc = harvestLogWriter.logEntry(

				harvester.getHarvestUid(),

				startTime,

				endTime,

				recordCount,

				resumptionCount,

				HarvestLogWriter.COMPLETED_SUCCESSFUL,

				harvester.getHarvestedRecordsDir(),

				zipFilePathName,

				supportedGranularity,

				deletedRecordSupport,

				null,

				msg);



		Document[] addDocs = new Document[]{doc};

		harvestLogIndex.update(

				"harvestuid",

				Long.toString(harvester.getHarvestUid()),

				addDocs,

				true);

	}





	/**

	 *  Gets the number of records the Harveser should harvest between sending statusMessage

	 *  notifications to this HarvestMessageHandler.

	 *

	 * @return    The numRecordsForStatusNotification value.

	 * @see       #statusMessage(String msg)

	 */

	public int getNumRecordsForStatusNotification() {

		return numRecordsForNotification;

	}





	// ---------------------- Debug info --------------------



	/**

	 *  Return a string for the current time and date, sutiable for display in log files and

	 *  output to standout:

	 *

	 * @return    The dateStamp value

	 */

	protected final static String getDateStamp() {

		return

				new SimpleDateFormat("MMM d, yyyy h:mm:ss a zzz").format(new Date());

	}





	/**

	 *  Output a line of text to error out, with datestamp.

	 *

	 * @param  s  The text that will be output to error out.

	 */

	private final void prtlnErr(String s) {

		System.err.println(getDateStamp() + " " + s);

	}







	/**

	 *  Output a line of text to standard out, with datestamp, if debug is set to true.

	 *

	 * @param  s  The String that will be output.

	 */

	private final void prtln(String s) {

		if (debug) {

			System.out.println(getDateStamp() + " " + s);

		}

	}





	/**

	 *  Sets the debug attribute of the object

	 *

	 * @param  db  The new debug value

	 */

	public static void setDebug(boolean db) {

		debug = db;

	}

}



