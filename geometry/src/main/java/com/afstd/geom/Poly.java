/*
 * This license does NOT supersede the original license of GPC.  Please see:
 * http://www.cs.man.ac.uk/~toby/alan/software/#Licensing
 *
 * The SEI Software Open Source License, Version 1.0
 *
 * Copyright (c) 2004, Solution Engineering, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Solution Engineering, Inc. (http://www.seisw.com/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 3. The name "Solution Engineering" must not be used to endorse or
 *    promote products derived from this software without prior
 *    written permission. For written permission, please contact
 *    admin@seisw.com.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL SOLUTION ENGINEERING, INC. OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 */

package com.afstd.geom;

/**
 * <code>Poly</code> is an interface to a complex polygon.  <code>Poly</code> polygons
 * can consist of  multiple "inner" polygons that can be disjoint and can be considered holes.
 * <p>
 * Currently, this interface supports two concepts:
 * <ul>
 *    <li>a set of inner polygons</li>
 *    <li>a set of points of a polygon</li>
 * </ul>
 * <p>
 * <b>Refactoring.</b> This would be a good place for some refactoring to create
 * a ComplexPoly and an InnerPoly or something so that these two concepts are broken
 * out.  One might also consider changing from an interface to an abstract class,
 * so the methods <code>isContributing()</code> and <code>setContributing()</code>
 * could have an access of package only.  Or, the <code>Clip</code> algorithm could 
 * not store this information in the <code>Poly</code>.
 * <p>
 * <b>Assumptions.</b> The methods that access the polygon as though it were a set of points assume
 * it is accessing the first polygon in the list of inner polygons.  It is also assumed that
 * inner polygons do not have more inner polygons.
 *
 * @author  Dan Bridenbecker, Solution Engineering, Inc.
 */
public interface Poly
{
   // ----------------------
   // --- Public Methods ---
   // ----------------------
   /**
    * Remove all of the points.  Creates an empty polygon.
    */
   public void clear();
   
   /**
    * Add a point to the first inner polygon.
    */
   public void add(double x, double y);
   
   /**
    * Add a point to the first inner polygon.
    */
   public void add(Point2D p);
   
   /**
    * Add an inner polygon to this polygon - assumes that adding polygon does not
    * have any inner polygons.
    */
   public void add(Poly p);
   
   /**
    * Return true if the polygon is empty
    */
   public boolean isEmpty();
   
   /**
    * Returns the bounding rectangle of this polygon.
    */
   public Rectangle2D getBounds();
   
   /**
    * Returns the polygon at this index.
    */
   public Poly getInnerPoly(int polyIndex);
   
   /**
    * Returns the number of inner polygons - inner polygons are assumed to return one here.
    */
   public int getNumInnerPoly();   
   
   /**
    * Return the number points of the first inner polygon
    */
   public int getNumPoints();
   
   /**
    * Return the X value of the point at the index in the first inner polygon
    */
   public double getX(int index);
   
   /**
    * Return the Y value of the point at the index in the first inner polygon
    */
   public double getY(int index);
   
   /**
    * Return true if this polygon is a hole.  Holes are assumed to be inner polygons of
    * a more complex polygon.
    *
    * @throws IllegalStateException if called on a complex polygon.
    */
   public boolean isHole();
   
   /**
    * Set whether or not this polygon is a hole.  Cannot be called on a complex polygon.
    *
    * @throws IllegalStateException if called on a complex polygon.
    */
   public void setIsHole(boolean isHole);
   
   /**
    * Return true if the given inner polygon is contributing to the set operation.
    * This method should NOT be used outside the Clip algorithm.
    */
   public boolean isContributing(int polyIndex);
   
   /**
    * Set whether or not this inner polygon is constributing to the set operation.
    * This method should NOT be used outside the Clip algorithm.
    */
   public void setContributing(int polyIndex, boolean contributes);
   
   /**
    * Return a Poly that is the intersection of this polygon with the given polygon.
    * The returned polygon could be complex.
    */
   public Poly intersection(Poly p);
      
   /**
    * Return a Poly that is the union of this polygon with the given polygon.
    * The returned polygon could be complex.
    */
   public Poly union(Poly p);
   
   /**
    * Return a Poly that is the exclusive-or of this polygon with the given polygon.
    * The returned polygon could be complex.
    */
   public Poly xor(Poly p);
   
   /**
    * Return a Poly that is the difference of this polygon with the given polygon.
    * The returned polygon could be complex.
    */
   public Poly difference(Poly p);
   
   /**
    * Return the area of the polygon in square units.
    */
   public double getArea();
}
