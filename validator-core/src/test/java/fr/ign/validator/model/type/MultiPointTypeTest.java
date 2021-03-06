package fr.ign.validator.model.type;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPoint;
import junit.framework.TestCase;

public class MultiPointTypeTest extends TestCase {

	private MultiPointType pointType = new MultiPointType() ;
	
	public void testBindEmpty(){
		Geometry geometry = pointType.bind("GEOMETRYCOLLECTION EMPTY") ;
		assertTrue( geometry.isEmpty() );
	}
	
	public void testBindPoint(){
		Geometry geometry = pointType.bind("POINT(3.0 4.0)") ;
		assertFalse( geometry.isEmpty() );
		assertTrue(geometry instanceof MultiPoint);
		assertEquals(1,geometry.getNumGeometries());
	}
	
	public void testBindMultiPoint(){
		Geometry geometry = pointType.bind("MULTIPOINT(3.0 4.0,5.0 6.0)") ;
		assertFalse( geometry.isEmpty() );
		assertTrue(geometry instanceof MultiPoint);
		assertEquals(2,geometry.getNumGeometries());
	}

// TODO improve to allow this
//	public void testBindFakeGeometryCollection(){
//		Geometry geometry = pointType.bind("GEOMETRYCOLLECTION(POINT(3.0 4.0),POINT(5.0 6.0))") ;
//		assertFalse( geometry.isEmpty() );
//		assertTrue(geometry instanceof MultiPoint);
//		assertEquals(2,geometry.getNumGeometries());
//	}

	
	public void testBindLineString(){
		boolean throwException = false ;
		try {
			pointType.bind("LINESTRING(2.0 3.0,4.0 5.0)") ;
		}catch (IllegalArgumentException e){
			throwException = true ;
		}
		assertTrue(throwException);
	}
	
	
	public void testBindRealGeometryCollection(){
		boolean throwException = false ;
		try {
			pointType.bind("GEOMETRYCOLLECTION(POINT(3.0 4.0),LINESTRING(3.0 4.0,5.0 6.0))") ;
		}catch (IllegalArgumentException e){
			throwException = true ;
		}
		assertTrue(throwException);
	}
}

