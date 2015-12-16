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

import java.util.ArrayList;
import java.util.List;

/**
 * <code>PolySimple</code> is a simple polygon - contains only one inner polygon.
 * <p>
 * <strong>WARNING:</strong> This type of <code>Poly</code> cannot be used for an
 * inner polygon that is a hole.
 *
 * @author  Dan Bridenbecker, Solution Engineering, Inc.
 */
public class PolySimple implements Poly
{
   // -----------------
   // --- Constants ---
   // -----------------
   
   // ------------------------
   // --- Member Variables ---
   // ------------------------
   /**
    * The list of Point2D objects in the polygon.
    */
   protected List<Point2D> m_List = new ArrayList<Point2D>();

   /** Flag used by the Clip algorithm */
   private boolean m_Contributes = true ;
   
   // --------------------
   // --- Constructors ---
   // --------------------
   /** Creates a new instance of PolySimple */
   public PolySimple()
   {
   }
   
   // ----------------------
   // --- Object Methods ---
   // ----------------------
   /**
    * Return true if the given object is equal to this one.
    * <p>
    * <strong>WARNING:</strong> This method failse if the first point
    * appears more than once in the list.
    */
   public boolean equals( Object obj )
   {
      if( !(obj instanceof PolySimple) )
      {
         return false;
      }
      PolySimple that = (PolySimple)obj;
      
      int this_num = this.m_List.size();
      int that_num = that.m_List.size();
      if( this_num != that_num ) return false ;
      
      // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
      // !!! WARNING: This is not the greatest algorithm.  It fails if !!!
      // !!! the first point in "this" poly appears more than once.    !!!
      // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
      if( this_num > 0 )
      {
         double this_x = this.getX(0);
         double this_y = this.getY(0);
         int that_first_index = -1 ;
         for( int that_index = 0 ; (that_first_index == -1) && (that_index < that_num) ; that_index++ )
         {
            double that_x = that.getX(that_index);
            double that_y = that.getY(that_index);
            if( (this_x == that_x) && (this_y == that_y) )
            {
               that_first_index = that_index ;
            }
         }
         if( that_first_index == -1 ) return false ;
         int that_index = that_first_index ;
         for( int this_index = 0 ; this_index < this_num ; this_index++ )
         {
            this_x = this.getX(this_index);
            this_y = this.getY(this_index);
            double that_x = that.getX(that_index);
            double that_y = that.getY(that_index);
            
            if( (this_x != that_x) || (this_y != that_y) ) return false;
               
            that_index++ ;
            if( that_index >= that_num )
            {
               that_index = 0 ;
            }
         }
      }
      return true ;
   }
   
   /**
    * Return the hashCode of the object.
    * <p>
    * <strong>WARNING:</strong>Hash and Equals break contract.
    *
    * @return an integer value that is the same for two objects
    * whenever their internal representation is the same (equals() is true)
    */
   public int hashCode()
   {
      // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
      // !!! WARNING:  This hash and equals break the contract. !!!
      // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
      int result = 17 ;
      result = 37*result + m_List.hashCode();
      return result;
   }
   
   /**
    * Return a string briefly describing the polygon.
    */
   public String toString()
   {
      return "PolySimple: num_points="+getNumPoints();
   }
   
   // --------------------
   // --- Poly Methods ---
   // --------------------
   /**
    * Remove all of the points.  Creates an empty polygon.
    */
   public void clear()
   {
      m_List.clear();
   }
   
   /**
    * Add a point to the first inner polygon.
    */
   public void add(double x, double y)
   {
      add( new Point2D( x, y ) );
   }
   
   /**
    * Add a point to the first inner polygon.
    */
   public void add(Point2D p)
   {
      m_List.add( p );
   }
   
   /**
    * Throws IllegalStateexception if called
    */
   public void add(Poly p)
   {
      throw new IllegalStateException("Cannot add poly to a simple poly.");
   }
   
   /**
    * Return true if the polygon is empty
    */
   public boolean isEmpty()
   {
      return m_List.isEmpty();
   }
   
   /**
    * Returns the bounding rectangle of this polygon.
    */
   public Rectangle2D getBounds()
   {
      double xmin =  Double.MAX_VALUE ;
      double ymin =  Double.MAX_VALUE ;
      double xmax = -Double.MAX_VALUE ;
      double ymax = -Double.MAX_VALUE ;
      
      for( int i = 0 ; i < m_List.size() ; i++ )
      {
         double x = getX(i);
         double y = getY(i);
         if( x < xmin ) xmin = x;
         if( x > xmax ) xmax = x;
         if( y < ymin ) ymin = y;
         if( y > ymax ) ymax = y;
      }
      
      return new Rectangle2D( xmin, ymin, (xmax-xmin), (ymax-ymin) );
   }
   
   /**
    * Returns <code>this</code> if <code>polyIndex = 0</code>, else it throws
    * IllegalStateException.
    */
   public Poly getInnerPoly(int polyIndex)
   {
      if( polyIndex != 0 )
      {
         throw new IllegalStateException("PolySimple only has one poly");
      }
      return this ;
   }
   
   /**
    * Always returns 1.
    */
   public int getNumInnerPoly()
   {
      return 1 ;
   }
   
   /**
    * Return the number points of the first inner polygon
    */
   public int getNumPoints()
   {
      return m_List.size();
   }   

   /**
    * Return the X value of the point at the index in the first inner polygon
    */
   public double getX(int index)
   {
      return m_List.get(index).getX();
   }
   
   /**
    * Return the Y value of the point at the index in the first inner polygon
    */
   public double getY(int index)
   {
      return m_List.get(index).getY();
   }
   
   /**
    * Always returns false since PolySimples cannot be holes.
    */
   public boolean isHole()
   {
      return false ;
   }
   
   /**
    * Throws IllegalStateException if called.
    */
   public void setIsHole(boolean isHole)
   {
      throw new IllegalStateException("PolySimple cannot be a hole");
   }
   
   /**
    * Return true if the given inner polygon is contributing to the set operation.
    * This method should NOT be used outside the Clip algorithm.
    *
    * @throws IllegalStateException if <code>polyIndex != 0</code>
    */
   public boolean isContributing( int polyIndex )
   {
      if( polyIndex != 0 )
      {
         throw new IllegalStateException("PolySimple only has one poly");
      }
      return m_Contributes ;
   }
   
   /**
    * Set whether or not this inner polygon is constributing to the set operation.
    * This method should NOT be used outside the Clip algorithm.
    *
    * @throws IllegalStateException if <code>polyIndex != 0</code>
    */
   public void setContributing( int polyIndex, boolean contributes )
   {
      if( polyIndex != 0 )
      {
         throw new IllegalStateException("PolySimple only has one poly");
      }
      m_Contributes = contributes ;
   }
   
   /**
    * Return a Poly that is the intersection of this polygon with the given polygon.
    * The returned polygon is simple.
    *
    * @return The returned Poly is of type PolySimple
    */
   public Poly intersection(Poly p)
   {
      return Clip.intersection( this, p, this.getClass() );
   }
   
   /**
    * Return a Poly that is the union of this polygon with the given polygon.
    * The returned polygon is simple.
    *
    * @return The returned Poly is of type PolySimple
    */
   public Poly union(Poly p)
   {
      return Clip.union( this, p, this.getClass() );
   }
   
   /**
    * Return a Poly that is the exclusive-or of this polygon with the given polygon.
    * The returned polygon is simple.
    *
    * @return The returned Poly is of type PolySimple
    */
   public Poly xor(Poly p)
   {
      return Clip.xor( p, this, this.getClass() );
   }
         
   /**
    * Return a Poly that is the exclusive-or of this polygon with the given polygon.
    * The returned polygon is simple.
    *
    * @return The returned Poly is of type PolySimple
    */
   public Poly difference(Poly p)
   {
      return Clip.difference( this, p, this.getClass() );
   }
         
   /**
    * Returns the area of the polygon.
    * <p>
    * The algorithm for the area of a complex polygon was take from
    * code by Joseph O'Rourke author of " Computational Geometry in C".
    */
   public double getArea()
   {
      if( getNumPoints() < 3 )
      {
         return 0.0 ;
      }
      double ax = getX(0);
      double ay = getY(0);
      double area = 0.0 ;
      for( int i = 1 ; i < (getNumPoints()-1) ; i++ )
      {
         double bx = getX(i);
         double by = getY(i);
         double cx = getX(i+1);
         double cy = getY(i+1);
         double tarea = ((cx - bx)*(ay - by)) - ((ax - bx)*(cy - by));
         area += tarea ;
      }
      area = 0.5*Math.abs(area);
      return area ;
   }
   
   // -----------------------
   // --- Package Methods ---
   // -----------------------
}
