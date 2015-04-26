
	/** This is a generic version of C.A.R Hoare's Quick Sort
    * algorithm.  This will handle arrays that are already
    * sorted, and arrays with duplicate keys.<BR>
    *
    * If you think of a one dimensional array as going from
    * the lowest index on the left to the highest index on the right
    * then the parameters to this function are lowest index or
    * left and highest index or right.  The first time you call
    * this function it will be with the parameters 0, a.length - 1.
    *
    * @param a       an integer array
    * @param lo0     left boundary of array partition
    * @param hi0     right boundary of array partition
    */

import java.util.*;
import java.io.*;

public class Sorter
{
	public static Vector sort(Vector a)                
    {
        int n = a.size();
            
		ISortable[] objs = new ISortable[n];
		
		for ( int i = 0; i < n; ++i )
			objs[i] = (ISortable) a.elementAt(i);
		
		Sorter.sort(objs);
		
		Vector vo = new Vector();
		
		for ( int i = 0; i < n; ++i )
			vo.addElement(objs[i]);
		
		return vo;
    }
	
	public static void sort(ISortable a[])
	{
		sort(a, 0, a.length - 1);
	}
	
   private static void sort(ISortable a[], int lo0, int hi0)
   {
      int lo = lo0;
      int hi = hi0;
      ISortable mid;

      if (hi0 > lo0)
      {
         mid = a[ ( lo0 + hi0 ) / 2 ];

         // loop through the array until indices cross
         while( lo <= hi )
         {
        	 while( ( lo < hi0 ) && ( a[lo].compareTo(mid) < 0 ) )
        		 ++lo;

        	 while( ( hi > lo0 ) && ( a[hi].compareTo(mid) > 0 ) )
        		 --hi;

            // if the indexes have not crossed, swap
            if( lo <= hi )
            {
               swap(a, lo, hi);
               ++lo;
               --hi;
            }
         }

         if( lo0 < hi )
            sort( a, lo0, hi );

         if( lo < hi0 )
            sort( a, lo, hi0 );
      }
   }

   private static void swap(ISortable a[], int i, int j)
   {
      ISortable T;
      T = a[i];
      a[i] = a[j];
      a[j] = T;
   }
   
   class TempSortItem implements ISortable
   {
   	Object thing;
   	long id;
   	
   	TempSortItem(Object o, long i)		
   	{
   		thing = o;
   		id = i;
   	}
   	
   	public int compareTo(ISortable b)
   	{
   		TempSortItem in = (TempSortItem) b;
   		
   		if ( id > in.id ) return -1;
   		if ( id < in.id ) return 1;
   		
   		return 0;
   	}
   }
}


