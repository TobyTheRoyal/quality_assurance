package at.tugraz.ist.qs2021.simple

object SimpleFunctions {

  // -------------------------------------------------------------------------------------------------------------------
  // Insertion sort

  /**
   * Insertion sort
   *
   * @param xs List to sort
   * @return sorted list
   */
  def insertionSort(xs: List[Int]): List[Int] = xs match {
    case Nil => Nil
    case a :: as => insert(a, insertionSort(as))
  }


  /**
   * Insertion sort helper method.
   * Inserts a new element x into already sorted list xs
   *
   * @param x  new element to insert
   * @param xs already sorted list
   * @return
   */
  private def insert(x: Int, xs: List[Int]): List[Int] = xs match {
    case Nil => List(x)
    case a :: as =>
      if (a >= x) x :: xs
      else a :: insert(x, as)
  }

  // -------------------------------------------------------------------------------------------------------------------
  // Maximum

  /**
   * Get largest integer/maximum of list
   *
   * @param xs a non-empty list
   * @return largest integer
   */
  def max(xs: List[Int]): Int = xs match {
    case a :: as => max(a, as)
  }

  /**
   * Tail recursive helper function for maximum.
   * Gives maximum of integer x and list xs
   *
   * @param x  a integer
   * @param xs a list
   * @return maximum of x and xs
   */
  @scala.annotation.tailrec
  private def max(x: Int, xs: List[Int]): Int = xs match {
    case Nil => x
    case a :: as =>
      if (x >= a) max(x, as)
      else max(a, as)
  }

  // -------------------------------------------------------------------------------------------------------------------
  // Min Index

  /**
   * Get the index of the smallest element of a list
   *
   * @param xs a non-empty list
   * @return the index of the smallest element
   */
  def minIndex(xs: List[Int]): Int = xs match {
    case a :: as => minIndex(as, 0, a, 1)
  }

  /**
   * Tail recursive helper function for minIndex.
   *
   * @param xs           a list
   * @param minimalIndex the current index of the minimal value
   * @param minimalValue the current minimal value
   * @param currentIndex the list index of the current element
   * @return the index of the smallest element
   */
  @scala.annotation.tailrec
  private def minIndex(xs: List[Int], minimalIndex: Int, minimalValue: Int, currentIndex: Int): Int = xs match {
    case Nil => minimalIndex
    case a :: as =>
      if (a < minimalValue) minIndex(as, currentIndex, a, currentIndex + 1)
      else minIndex(as, minimalIndex, minimalValue, currentIndex + 1)
  }

  // -------------------------------------------------------------------------------------------------------------------
  // Symmetric Difference

  /**
   * Get a set of elements that are in one of the two sets, but not in both
   *
   * @param xs first set (as List)
   * @param ys second set (as List)
   * @return the symmetric difference of xs and ys (as List)
   */
  def symmetricDifference(xs: List[Int], ys: List[Int]): List[Int] =
    (symmetricDifference(xs, ys, Nil) ++ symmetricDifference(ys, xs, Nil)).distinct


  /**
   * Tail recursive helper function for symmetric difference.
   * Adds elements of addList to the result, that are not in checkList
   *
   * @param addList   list of elements to add to result
   * @param checkList list of elements to compare
   * @param result    intermediate result
   * @return addList without checklist
   */
  @scala.annotation.tailrec
  private def symmetricDifference(addList: List[Int], checkList: List[Int], result: List[Int]): List[Int] =
    addList match {
      case Nil => result.reverse
      case a :: as =>
        if (checkList.contains(a)) symmetricDifference(as, checkList, result)
        else symmetricDifference(as, checkList, a :: result)
    }

  // -------------------------------------------------------------------------------------------------------------------
  // Intersection

  /**
   * Get a set of elements that are in both sets
   *
   * @param xs first set (as List)
   * @param ys second set (as List)
   * @return intersection of xs and ys
   */
  def intersection(xs: List[Int], ys: List[Int]): List[Int] =
    intersection(xs, ys, Nil).distinct

  /**
   * Tail recursive helper function for intersection.
   * Adds elements of addList to the result, that are in checkList
   *
   * @param addList   list of elements to add to result
   * @param checkList list of elements to compare
   * @param result    intermediate result
   * @return addList intersected with checklist
   */
  @scala.annotation.tailrec
  private def intersection(addList: List[Int], checkList: List[Int], result: List[Int]): List[Int] =
    addList match {
      case Nil => result
      case a :: as =>
        if (checkList.contains(a)) intersection(as, checkList, a :: result)
        else intersection(as, checkList, result)
    }


  // -------------------------------------------------------------------------------------------------------------------
  // Smallest missing positive integer

  /**
   * Gives the smallest integer > 0 that is not in the given list.
   *
   * @param xs the list to check
   * @return smallest missing positive integer
   */
  def smallestMissingPositiveInteger(xs: List[Int]): Int = {
    val arrayCopy: Array[Int] = xs.toArray

    var j: Int = 0
    for (i <- arrayCopy.indices) {
      if (arrayCopy(i) <= 0) {
        val temp = arrayCopy(i)
        arrayCopy(i) = arrayCopy(j)
        arrayCopy(j) = temp
        j += 1
      }
    }

    val array2 = arrayCopy.slice(j, arrayCopy.length)

    for (i <- array2.indices) {
      j = math.abs(array2(i)) - 1
      if (j < array2.length && array2(j) > 0) {
        array2(j) = -array2(j)
      }
    }

    for (i <- array2.indices) {
      if (array2(i) > 0) {
        return i + 1
      }
    }

    array2.length + 1
  }

}
