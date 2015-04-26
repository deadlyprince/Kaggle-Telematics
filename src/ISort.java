

public class ISort 
{
	   static void sort(int a[], int lo0, int hi0)
	   {
	      int lo = lo0;
	      int hi = hi0;
	      int mid;

	      if ( hi0 > lo0 )
	      {

	         /* Arbitrarily establishing partition element as the midpoint of
	          * the array.
	          */
	         mid = a[ ( lo0 + hi0 ) / 2 ];

	         // loop through the array until indices cross
	         while( lo <= hi )
	         {
	            /* find the first element that is greater than or equal to
	             * the partition element starting from the left Index.
	             */
		     while( ( lo < hi0 ) && ( a[lo] < mid ) )
			 ++lo;

	            /* find an element that is smaller than or equal to
	             * the partition element starting from the right Index.
	             */
		     while( ( hi > lo0 ) && ( a[hi] > mid ) )
			 --hi;

	            // if the indexes have not crossed, swap
	            if( lo <= hi )
	            {
	               swap(a, lo, hi);
	               ++lo;
	               --hi;
	            }
	         }

	         /* If the right index has not reached the left side of array
	          * must now sort the left partition.
	          */
	         if( lo0 < hi )
	            sort( a, lo0, hi );

	         /* If the left index has not reached the right side of array
	          * must now sort the right partition.
	          */
	         if( lo < hi0 )
	            sort( a, lo, hi0 );
	      }
	   }

	   static private void swap(int a[], int i, int j)
	   {
	      int T;
	      T = a[i];
	      a[i] = a[j];
	      a[j] = T;
	   }

	   static public void sort(int a[])
	   {
	      sort(a, 0, a.length - 1);
	   }
	   
	   
	   
	   
	   
	   
	   
	   static void fsort(float a[], int lo0, int hi0)
	   {
	      int lo = lo0;
	      int hi = hi0;
	      float mid;

	      if ( hi0 > lo0 )
	      {

	         /* Arbitrarily establishing partition element as the midpoint of
	          * the array.
	          */
	         mid = a[ ( lo0 + hi0 ) / 2 ];

	         // loop through the array until indices cross
	         while( lo <= hi )
	         {
	            /* find the first element that is greater than or equal to
	             * the partition element starting from the left Index.
	             */
		     while( ( lo < hi0 ) && ( a[lo] < mid ) )
			 ++lo;

	            /* find an element that is smaller than or equal to
	             * the partition element starting from the right Index.
	             */
		     while( ( hi > lo0 ) && ( a[hi] > mid ) )
			 --hi;

	            // if the indexes have not crossed, swap
	            if( lo <= hi )
	            {
	               fswap(a, lo, hi);
	               ++lo;
	               --hi;
	            }
	         }

	         /* If the right index has not reached the left side of array
	          * must now sort the left partition.
	          */
	         if( lo0 < hi )
	            fsort( a, lo0, hi );

	         /* If the left index has not reached the right side of array
	          * must now sort the right partition.
	          */
	         if( lo < hi0 )
	            fsort( a, lo, hi0 );
	      }
	   }

	   static private void fswap(float a[], int i, int j)
	   {
	      float T;
	      T = a[i];
	      a[i] = a[j];
	      a[j] = T;
	   }

	   static public void fsort(float a[])
	   {
	      fsort(a, 0, a.length - 1);
	   }
}
