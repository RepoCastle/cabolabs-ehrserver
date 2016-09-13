package com.cabolabs.ehrserver.account

import com.cabolabs.ehrserver.openehr.common.change_control.Contribution
import com.cabolabs.ehrserver.openehr.common.change_control.Version

class StatsController {

   def versionFSRepoService

   /**
    * Show stats dashboard for all the organizations accessible by the user.
    */
   def index()
   {
   }
   
   /**
    * Show detailed stats for an organization in an interval of time.
    * Dates come as epoch times.
    */
   def organization(String uid, long from, long to)
   {
      // If not date range is set, set the rante to the current month
      if (!to)
      {
         def cal = Calendar.getInstance()
         //cal.add(Calendar.MONTH, -1)
         cal.set(Calendar.DATE, 1)
         
         from = cal.getTimeInMillis()
         
         cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE))
         
         to = cal.getTimeInMillis()
      }
   
      println from
      println to
   
      def dfrom = new Date(from)
      def dto   = new Date(to)
      
      println dfrom
      println dto
   
      // Number of transactions
      def contributions = Contribution.withCriteria {
        
        projections {
           count()
        }
        
        eq('organizationUid', uid)
        audit {
          between('timeCommitted', dfrom, dto)
        }
      }
      
      // Number of documents and size in bytes (more than one document per transaction is allowed)
      def versions = Version.withCriteria {
         
         projections {
           count()
         }
        
         contribution {
            eq('organizationUid', uid)
         }
         commitAudit {
            between('timeCommitted', dfrom, dto)
         }
      }
      
      def size = versionFSRepoService.getRepoSizeInBytesBetween(uid, dfrom, dto)
      
      [transactions: contributions, documents: versions, size: size]
   }
}
