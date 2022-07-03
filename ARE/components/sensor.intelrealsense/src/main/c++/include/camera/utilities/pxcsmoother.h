/*******************************************************************************                                                                                                                                                                                                                          /*******************************************************************************

INTEL CORPORATION PROPRIETARY INFORMATION
This software is supplied under the terms of a license agreement or nondisclosure
agreement with Intel Corporation and may not be copied or disclosed except in
accordance with the terms of that agreement
Copyright(c) 2011-2015 Intel Corporation. All Rights Reserved.

*******************************************************************************/
/** @file PXCSmoother.h
    Defines the PXCSmoother interface, which allows to smooth any data using different algorithms
 */
#pragma once
#include "pxcbase.h"

/** @class PXCSmoother 
	A utility that allows smoothing data of different types, using a variety of algorithms
	Stabilizer Smoother – The stabilizer smoother keeps the smoothed data point stable as long as it has not moved more than a given threshold. 
	Weighted Smoother – The weighted smoother applies a (possibly) different weight to each of the previous data samples. 
	Quadratic Smoother – The quadratic smoother is a time based smoother ideal for UI (User Interface) purposes. 
	Spring Smoother – The spring smoother is a time based smoother ideal for gaming purposes. 
*/
class PXCSmoother: public PXCBase 
{
public:

	/* Constants */
	PXC_CUID_OVERWRITE(PXC_UID('S','U','M','O')); 
	
	/** @class Smoother1D
		Handles the smoothing of a stream of floats, using a specific smoothing algorithm
	*/
	class Smoother1D: public PXCBase
	{
	public:
		/* Constants */
		PXC_CUID_OVERWRITE(PXC_UID('S','U','M',1)); 

		/**			
			@brief Add a new data sample to the smoothing algorithm
			@param[in] value the latest data sample 
			@return smoothed value in pxcF32 format
		*/		
		virtual pxcF32 PXCAPI SmoothValue(pxcF32 value) = 0;
		/**			
			@brief Reset smoother algorithm data
		*/	
		virtual void PXCAPI Reset() = 0;
	};

	/** @class Smoother2D
		Handles the smoothing of a stream of 2D points, using a specific smoothing algorithm
	*/
	class Smoother2D: public PXCBase
	{
	public:
		/* Constants */
		PXC_CUID_OVERWRITE(PXC_UID('S','U','M',2)); 

		/**			
			@brief Add a new data sample to the smoothing algorithm
			@param[in] value the latest data sample 
			@return smoothed value in PXCPointF32 format	
		*/		
		virtual PXCPointF32 PXCAPI SmoothValue(const PXCPointF32 & value) = 0;
		/**			
			@brief Reset smoother algorithm data
		*/	
		virtual void PXCAPI Reset() = 0;
	};

	/** @class Smoother3D
		Handles the smoothing of a stream of 3D points, using a specific smoothing algorithm
	*/
	class Smoother3D: public PXCBase
	{
	public:
		/* Constants */
		PXC_CUID_OVERWRITE(PXC_UID('S','U','M',3)); 

		/**			
			@brief Add a new data sample to the smoothing algorithm
			@param[in] value the latest data sample 
			@return smoothed value in PXCPoint3DF32 format		
		*/		
		virtual PXCPoint3DF32 PXCAPI SmoothValue(const PXCPoint3DF32 & value) = 0;
		/**			
			@brief Reset smoother algorithm data
		*/	
		virtual void PXCAPI Reset() = 0;
	};
	
		/** @brief Create Stabilizer smoother instance for single floats
			The stabilizer keeps the smoothed data point stable as long as it has not moved more than a given threshold
			@param[in] stabilizeStrength The stabilizer smoother strength, default value is 0.5f
			@param[in] stabilizeRadius The stabilizer smoother radius in correlation to the input unit value
			@return a pointer to the created Smoother, or NULL in case of illegal arguments
		*/
	virtual Smoother1D*	PXCAPI Create1DStabilizer(pxcF32 stabilizeStrength, pxcF32 stabilizeRadius)=0;
	__inline Smoother1D* Create1DStabilizer(pxcF32 stabilizeRadius) { return Create1DStabilizer(0.5f, stabilizeRadius); }
		
		/** @brief Create the Weighted algorithm for single floats
			The Weighted algorithm applies a (possibly) different weight to each of the previous data samples
			If the weights vector is not assigned (NULL) all the weights will be equal (1/numWeights)
			@param[in] numWeights The Weighted smoother number of weights
			@param[in] weights The Weighted smoother weight values
			@return a pointer to the created Smoother, or NULL in case of illegal arguments
		*/
	virtual Smoother1D*	PXCAPI Create1DWeighted(pxcI32 numWeights, const pxcF32* weights)=0;
	__inline Smoother1D* Create1DWeighted(pxcI32 numWeights) { return Create1DWeighted(numWeights,0); }
		
		/** @brief Create the Quadratic algorithm for single floats
			@param[in] smoothStrength The Quadratic smoother smooth strength, default value is 0.5f
			@return a pointer to the created Smoother, or NULL in case of illegal arguments
		*/
	virtual Smoother1D*	PXCAPI Create1DQuadratic(pxcF32 smoothStrength)=0;
	__inline Smoother1D* Create1DQuadratic(void) { return Create1DQuadratic(0.5f); }
		
		/** @brief Create the Spring algorithm for single floats
			@param[in] smoothStrength The Spring smoother smooth strength, default value is 0.5f
			@return a pointer to the created Smoother, or NULL in case of illegal arguments
		*/
	virtual Smoother1D*	PXCAPI Create1DSpring(pxcF32 smoothStrength)=0;
	__inline Smoother1D* Create1DSpring(void) { return Create1DSpring(0.5f); }
		
		/** @brief Create Stabilizer smoother instance for 2-dimensional points
			The stabilizer keeps the smoothed data point stable as long as it has not moved more than a given threshold
			@param[in] stabilizeStrength The stabilizer smoother strength, default value is 0.5f
			@param[in] stabilizeRadius The stabilizer smoother radius in correlation to the input unit value
			@return a pointer to the created Smoother, or NULL in case of illegal arguments
		*/
	virtual Smoother2D*	PXCAPI Create2DStabilizer(pxcF32 stabilizeStrength, pxcF32 stabilizeRadius)=0;
	__inline Smoother2D* Create2DStabilizer(pxcF32 stabilizeRadius) { return Create2DStabilizer(0.5f,stabilizeRadius); }
		
		/** @brief Create the Weighted algorithm for 2-dimensional points
			The Weighted algorithm applies a (possibly) different weight to each of the previous data samples
			If the weights vector is not assigned (NULL) all the weights will be equal (1/numWeights)
			@param[in] numWeights The Weighted smoother number of weights
			@param[in] weights The Weighted smoother weight values
			@return a pointer to the created Smoother, or NULL in case of illegal arguments
		*/
	virtual Smoother2D*	PXCAPI Create2DWeighted(pxcI32 numWeights, const pxcF32* weights)=0;
	__inline Smoother2D* Create2DWeighted(pxcI32 numWeights) { return Create2DWeighted(numWeights,0); }
		
		/** @brief Create the Quadratic algorithm for 2-dimensional points
			@param[in] smoothStrength The Quadratic smoother smooth strength, default value is 0.5f
			@return a pointer to the created Smoother, or NULL in case of illegal arguments
		*/
	virtual Smoother2D*	PXCAPI Create2DQuadratic(pxcF32 smoothStrength)=0;
	__inline Smoother2D* Create2DQuadratic(void) { return Create2DQuadratic(0.5f); }
		
		/** @brief Create the Spring algorithm for 2-dimensional points
			@param[in] smoothStrength The Spring smoother smooth strength, default value is 0.5f
			@return a pointer to the created Smoother, or NULL in case of illegal arguments
		*/
	virtual Smoother2D*	PXCAPI Create2DSpring(pxcF32 smoothStrength)=0;
	__inline Smoother2D* Create2DSpring(void) { return Create2DSpring(0.5f); }

		/** @brief Create Stabilizer smoother instance for 3-dimensional points
			The stabilizer keeps the smoothed data point stable as long as it has not moved more than a given threshold
			@param[in] stabilizeStrength The stabilizer smoother strength, default value is 0.5f
			@param[in] stabilizeRadius The stabilizer smoother radius in correlation to the input unit value
			@return a pointer to the created Smoother, or NULL in case of illegal arguments
		*/
	virtual Smoother3D*	PXCAPI Create3DStabilizer(pxcF32 stabilizeStrength, pxcF32 stabilizeRadius)=0;
	__inline Smoother3D* Create3DStabilizer(pxcF32 stabilizeRadius) { return Create3DStabilizer(0.5f,stabilizeRadius); }
		
		/** @brief Create the Weighted algorithm for 3-dimensional points
			The Weighted algorithm applies a (possibly) different weight to each of the previous data samples
			If the weights vector is not assigned (NULL) all the weights will be equal (1/numWeights)
			@param[in] numWeights The Weighted smoother number of weights
			@param[in] weights The Weighted smoother weight values
			@return a pointer to the created Smoother, or NULL in case of illegal arguments
		*/
	virtual Smoother3D*	PXCAPI Create3DWeighted(pxcI32 numWeights, const pxcF32* weights)=0;
	__inline Smoother3D* Create3DWeighted(pxcI32 numWeights) { return Create3DWeighted(numWeights,0); }
		
		/** @brief Create the Quadratic algorithm for 3-dimensional points
			@param[in] smoothStrength The Quadratic smoother smooth strength, default value is 0.5f
			@return a pointer to the created Smoother, or NULL in case of illegal arguments
		*/
	virtual Smoother3D*	PXCAPI Create3DQuadratic(pxcF32 smoothStrength)=0;
	__inline Smoother3D* Create3DQuadratic(void) { return Create3DQuadratic(0.5f); }
		
		/** @brief Create the Spring algorithm for 3-dimensional points
			@param[in] smoothStrength The Spring smoother smooth strength, default value is 0.5f
			@return a pointer to the created Smoother, or NULL in case of illegal arguments
		*/
	virtual Smoother3D*	PXCAPI Create3DSpring(pxcF32 smoothStrength)=0;
	__inline Smoother3D* Create3DSpring(void) { return Create3DSpring(0.5f); }
};
