/*******************************************************************************

INTEL CORPORATION PROPRIETARY INFORMATION
This software is supplied under the terms of a license agreement or nondisclosure
agreement with Intel Corporation and may not be copied or disclosed except in
accordance with the terms of that agreement
Copyright(c) 2011-2015 Intel Corporation. All Rights Reserved.

*******************************************************************************/
#pragma once
#include "pxccapture.h"

/**
	Instance of this interface class can be created using 
	PXCScenePerception::CreatePXCSurfaceVoxelsData(...). ExportSurfaceVoxels
	function fills the data buffer. It's client's responsibility to 
	explicitly release the memory by calling Release() on PXCSurfaceVoxelsData.
*/
class PXCSurfaceVoxelsData : public PXCBase
{
public:

	PXC_CUID_OVERWRITE(PXC_UID('S', 'P', 'S', 'V'));

	/**
	    @brief: Returns number of surface voxels present in the buffer. 
		This function is expected to be used after successful
		call to ExportSurfaceVoxels().
	*/
	virtual pxcI32 PXCAPI QueryNumberOfSurfaceVoxels() = 0;

	/**
	    @brief: Returns an array to center of surface voxels extracted by 
		ExportSurfaceVoxels. This function is expected to be used after successful
		call to ExportSurfaceVoxels(). Valid range is [0, 3*QueryNumberOfSurfaceVoxels()).
	*/
	virtual pxcF32 * PXCAPI QueryCenterOfSurfaceVoxels() = 0;

	/**
	    @brief: Sets number of surface voxels to 0. However it doesn't release memory. 
	    It should be used when you reset scene perception using 
	    PXCScenePerception::Reset() client should Reset PXCSurfaceVoxelsData 
	    when scene perception is reset to stay in sync with the scene perception.
	*/
	virtual pxcStatus PXCAPI Reset() = 0;

	/**
	    @brief: Returns an array of colors with length 3*QueryNumberOfSurfaceVoxels(). Three
		color channels (RGB) per voxel. This function will return NULL, if 
		PXCSurfaceVoxelsData was created using 
		PXCScenePerception::CreatePXCSurfaceVoxelsData with bUseColor set to 0.
	*/
	virtual pxcBYTE * PXCAPI QuerySurfaceVoxelsColor() = 0;
};

/**
	An instance of this interface can be created using 
	PXCScenePerception::CreatePXCBlockMeshingData method DoMeshingUpdate
	function fills all the buffer with the data. It's client's responsibility to 
	explicitly release the memory by calling Release() on PXCBlockMeshingData.
*/
class PXCBlockMeshingData : public PXCBase 
{
	public:

	PXC_CUID_OVERWRITE(PXC_UID('S', 'P', 'B', 'M'));

	/**
	    @brief: Returns number of PXCBlockMesh present inside the buffer
		returned by QueryBlockMeshes(). This function is expected to be used 
		after successful call to DoMeshingUpdate(...).
	*/
	virtual pxcI32 PXCAPI QueryNumberOfBlockMeshes() = 0; 

	/**
	    @brief: Returns number of vertices present in the buffer returned by 
		QueryVertices(). This function is expected to be used after successful
		call to DoMeshingUpdate(...).
	*/
	virtual pxcI32 PXCAPI QueryNumberOfVertices() = 0; 
	
	/**
	    @brief: Returns number of faces in the buffer returned by 
		QueryFaces(). This function is expected to be used after successful
		call to DoMeshingUpdate(...).
	*/
	virtual pxcI32 PXCAPI QueryNumberOfFaces() = 0; 
	
	/**
	    @brief: Returns maximum number of PXCBlockMesh that can be returned by 
		DoMeshingUpdate. This value remains same throughout the lifetime of the
		instance.
	*/
	virtual pxcI32 PXCAPI QueryMaxNumberOfBlockMeshes() = 0;
	
	/**
	    @brief: Returns maximum number of vertices that can be returned by 
		PXCBlockMeshingData. This value remains same throughout the lifetime of
		the instance.
	*/
	virtual pxcI32 PXCAPI QueryMaxNumberOfVertices() = 0;
	
	/**
	    @brief: Returns maximum number of faces that can be returned by 
		PXCBlockMeshingData. This value remains same throughout the lifetime of 
		the instance.
	*/
	virtual pxcI32 PXCAPI QueryMaxNumberOfFaces() = 0;
	
	/**
	    Describes each BlockMesh present inside list returned by 
		QueryBlockMeshes().
	*/
	struct PXCBlockMesh
	{
		pxcI32 meshId;					// Unique ID to identify each PXCBlockMesh
		pxcI32 vertexStartIndex;		// Starting index of the vertex inside vertex buffer obtained using QueryVertices()
		pxcI32 numVertices;				// Total number of vertices inside this PXCBlockMesh

		pxcI32 faceStartIndex;			// Starting index of the face list in a MeshFaces buffer obtained using QueryFaces()
		pxcI32 numFaces;	 			// Number of faces forming the mesh inside this PXCBlockMesh 

		PXCPoint3DF32 min3dPoint;		// Minimum point for the axis aligned bounding box containing the mesh piece
		PXCPoint3DF32 max3dPoint;		// Maximum point for the axis aligned bounding box containing the mesh piece

		pxcF32 maxDistanceChange;		// Maximum change in the distance field due to accumulation since this block was last meshed
		pxcF32 avgDistanceChange;		// Average change in the distance field due to accumulation since this block was last meshed
	};

	/**
	    @brief: Returns an array of PXCBlockMesh objects with length same as 
		QueryNumberOfBlockMeshes().
	*/
	virtual PXCBlockMesh * PXCAPI QueryBlockMeshes() = 0;  
	
	/**
	    @brief: Returns an array of float points with length 4*QueryNumberOfVertices().
	    Each vertex consists of 4 float points: (x, y, z) coordinates in meter
		unit + a confidence value. The confidence value is in the range [0, 1] 
		indicating how confident scene perception is about the presence of 
		the vertex. 
	*/
	virtual pxcF32 *  PXCAPI QueryVertices() = 0;
	
	/**
	    @brief: Returns an array of colors with length 3*QueryNumberOfVertices(). Three
		color channels (RGB) per vertex. This function will return NULL, if 
		PXCBlockMeshingData was created using 
		PXCScenePerception::CreatePXCBlockMeshingData(...) with bUseColor set to 0.
	*/
	virtual pxcBYTE * PXCAPI QueryVerticesColor() = 0;
	
	/**
	    @brief: Returns an array of faces forming the mesh (3 pxcI32 indices 
		per triangle) valid range is from [0, 3*QueryNumberOfFaces()].
	*/
	virtual pxcI32  * PXCAPI QueryFaces() = 0;

	/**
	    @brief: Sets number of BlockMeshes, number of vertices and number of faces to
		0. However it doesn't release memory. It should be used when you reset 
		scene perception using PXCScenePerception::Reset(). Client should Reset 
		PXCBlockMeshingData when scene perception is Reset to stay in sync with
		the scene perception.
	*/
	virtual pxcStatus PXCAPI Reset() = 0;
};

class PXCScenePerception : public PXCBase
{
	public:
		PXC_CUID_OVERWRITE(PXC_UID('S', 'C', 'N', 'P'));
		
		enum TrackingAccuracy 
		{
			HIGH, 
			LOW, 
			MED, 
			FAILED
		};

		enum VoxelResolution
		{ 
			LOW_RESOLUTION, 
			MED_RESOLUTION, 
			HIGH_RESOLUTION
		};
	
		enum MeshResolution
		{
			LOW_RESOLUTION_MESH, 
			MED_RESOLUTION_MESH, 
			HIGH_RESOLUTION_MESH
		};

		typedef struct
		{
			pxcBool countOfBlockMeshesRequired;
			pxcBool blockMeshesRequired;
			pxcBool countOfVeticesRequired;
			pxcBool verticesRequired;
			pxcBool countOfFacesRequired;
			pxcBool facesRequired;
			pxcBool colorsRequired;
		}MeshingUpdateInfo;

		typedef struct  
		{
			PXCSizeI32  imageSize;
			PXCPointF32 focalLength;
			PXCPointF32 principalPoint;
		}ScenePerceptionIntrinsics;

		/**
			@brief: SetVoxelResolution sets volume resolution for the scene 
			perception. The VoxelResolution is locked when 
			PXCSenseManager.Init() is called.
			Afterwards value for VoxelResolution remains same throughout the 
			lifetime of PXCSenseManager. The default value of voxel 
			resolution is LOW_RESOLUTION.
		   
		    @param[in] voxelResolution: Resolution of the three dimensional 
			reconstruction. 
		    Possible values are:
			LOW_RESOLUTION:  For room-sized scenario (4/256m)
			MED_RESOLUTION:  For table-top-sized scenario (2/256m)
			HIGH_RESOLUTION: For object-sized scenario (1/256m)
		    Choosing HIGH_RESOLUTION in a room-size environment may degrade the
			tracking robustness and quality. Choosing LOW_RESOLUTION in an 
			object-sized scenario may result in a reconstructed model missing 
			the fine details. 
		   
		    @returns: PXC_STATUS_NO_ERROR if it succeeds, returns 
			PXC_STATUS_ITEM_UNAVAILABLE if called after making call to 
			PXCSenseManager::Init().
		*/
		virtual pxcStatus PXCAPI SetVoxelResolution(VoxelResolution /*voxelResolution*/) = 0;
		
		/**
			@brief: To get voxel resolution used by the scene 
			perception module. Please refer to SetVoxelResolution(...) for more details.
		
			@returns: Returns current value of VoxelResolution used by the 
			scene perception module
		*/
		virtual VoxelResolution PXCAPI QueryVoxelResolution() const = 0;

		/**
			@brief: Allows user to enable/disable integration of upcoming 
			camera stream into 3D volume. If disabled the volume will not be 
			updated. However scene perception will still keep tracking the 
			camera. This is a control parameter which can be updated before 
			passing every frame to the module.

			@param[in] enableFlag: Enable/Disable flag for integrating depth 
			data into the 3D volumetric representation.

		    @returns: PXC_STATUS_NO_ERROR if it succeeds, otherwise returns 
			the error code.
		*/
		virtual pxcStatus PXCAPI EnableSceneReconstruction(pxcBool /*enableFlag*/) = 0;

		/**
		    @brief: Allows user to check whether integration of upcoming 
			camera stream into 3D volume is enabled or disabled. 
			
			@returns: True, if integrating depth data into the 3D volumetric
			representation is enabled.
		*/
		virtual pxcBool   PXCAPI IsSceneReconstructionEnabled() = 0;

		/**
		    @brief: Allows user to set the initial camera pose.
		    This function is only available before first frame is passed to the
			module. Once the first frame is passed the initial camera pose is 
			locked and this function will be unavailable. If this function is 
			not used then the module uses default pose as the 
			initial pose for tracking for the device with no platform IMU and 
			for device with platform IMU the tracking pose will be computed 
			using gravity vector to align 3D volume with gravity when the 
			first frame is passed to the module.
		
		    @param[in] pose: Array of 12 pxcF32 that stores initial camera pose
		    user wishes to set in row-major order. Camera pose is specified in a 
			3 by 4 matrix [R | T] = [Rotation Matrix | Translation Vector]
		    where R = [ r11 r12 r13 ]
		              [ r21 r22 r23 ] 
		              [ r31 r32 r33 ]
		          T = [ tx  ty  tz  ]
		    Pose Array Layout = [r11 r12 r13 tx r21 r22 r23 ty r31 r32 r33 tz]
			Translation vector is in meters.
		    
		    @returns: If successful it returns PXC_STATUS_NO_ERROR,
			otherwise returns error code if invalid pose is passed or the 
			function is called after passing the first frame.
		*/
		virtual pxcStatus PXCAPI SetInitialPose(const pxcF32 pose[12]) = 0;
		
		/**
		    @brief: Allows user to get tracking accuracy of the last frame 
			processed by the module. We expect users to call this function 
			after successful PXCSenseManager::AcquireFrame(...) call and before 
			calling SenesManager::ReleaseFrame(). If tracking accuracy is FAILED 
			the volume data and camera pose are not updated.

			@returns: TrackingAccuracy which can be HIGH, LOW, MED or FAILED.
		*/
		virtual TrackingAccuracy PXCAPI QueryTrackingAccuracy() = 0;

		/**
		    @brief: Allows user to access camera's latest pose. The 
			correctness of the pose depends on value obtained from 
			QueryTrackingAccuracy().

			@param[out] pose: Array of 12 pxcF32 to store camera pose in 
			row-major order. Camera pose is specified in a 3 by 4 matrix 
			[R | T] = [Rotation Matrix | Translation Vector]
			where R = [ r11 r12 r13 ]
					  [ r21 r22 r23 ] 
					  [ r31 r32 r33 ]
				  T = [ tx  ty  tz  ]
			Pose Array Layout = [r11 r12 r13 tx r21 r22 r23 ty r31 r32 r33 tz]
			Translation vector is in meters.

			@returns: If successful it returns PXC_STATUS_NO_ERROR,
			Otherwise error code will be returned.
		*/
		virtual pxcStatus PXCAPI GetCameraPose(pxcF32 pose[12]) = 0;
		 
		/**
		    @brief: Allows user to check whether the 3D volume was updated 
			since last call to DoMeshingUpdate(...).
			This function is useful for determining when to call 
			DoMeshingUpdate. 

			@returns: flag indicating that reconstruction was updated.
		*/
		virtual pxcBool PXCAPI IsReconstructionUpdated() = 0;

		/**
		    @brief: Allows user to access 2D projection image of reconstructed 
			volume from a given camera pose by ray-casting. This function is 
			optimized for real time performance. It is also useful for 
			visualizing progress of the scene reconstruction. User should 
			explicitly call Release() on PXCImage after copying the data.
		    or before making subsequent call to QueryVolumePreview(...).
		   
		    @param[in] pose: Array of 12 pxcF32 that stores camera pose
		    in row-major order. Camera pose is specified in a 
		    3 by 4 matrix [R | T] = [Rotation Matrix | Translation Vector]
		    where R = [ r11 r12 r13 ]
			 		  [ r21 r22 r23 ] 
			 		  [ r31 r32 r33 ]
			      T = [ tx  ty  tz  ]
		    Pose Array Layout = [r11 r12 r13 tx r21 r22 r23 ty r31 r32 r33 tz]
		    Translation vector is in meters.
		   
		    @returns: Instance of PXCImage whose content can be used for volume
			rendering. Returns NULL if there is an internal state error	or when
			the rendering is failed or when an invalid pose is passed.
		*/
		virtual PXCImage * PXCAPI QueryVolumePreview(const pxcF32 pose[12]) = 0;

		/**
		    @brief: Reset removes all reconstructed model (volume) information 
			and the module will reinitialize the model when next stream is 
			passed to the module. It also resets the camera pose to the one 
			provided. If the pose is not provided then the module will use 
			default pose if	there is no platform IMU on the device and in case 
			of device with platform IMU the pose will be computed using gravity
			vector to align 3D volume with gravity when the next frame is 
			passed to the module. 
			
			However it doesn't Reset instance of PXCBlockMeshingData created using 
			PXCScenePerception::CreatePXCBlockMeshingData(...). User should 
			explicitly call PXCBlockMeshingData::Reset() to stay in sync with the 
		    reconstruction model inside scene perception.
		    
			@param[in] pose: Array of 12 pxcF32 that stores initial camera pose
			user wishes to set in row-major order. Camera pose is specified in a 
			3 by 4 matrix [R | T] = [Rotation Matrix | Translation Vector]
			where R = [ r11 r12 r13 ]
					  [ r21 r22 r23 ] 
					  [ r31 r32 r33 ]
				  T = [ tx  ty  tz  ]
			Pose Array Layout = [r11 r12 r13 tx r21 r22 r23 ty r31 r32 r33 tz]
			Translation vector is in meters.

		    @returns: On success returns PXC_STATUS_NO_ERROR. Otherwise returns
			error code like when an invalid pose argument is passed.
		*/
		virtual pxcStatus PXCAPI Reset(const pxcF32 pPose[12]) = 0;
		/**
		    @brief: Allows user to Reset scene perception with default Pose
		*/
		__inline pxcStatus Reset()
		{
			return Reset(0);
		}

		/**
		    @brief: Is an optional function meant for expert users. It allows 
			users to set meshing thresholds for DoMeshingUpdate(...).
		    The values set by this function will be used by succeeding calls to
			DoMeshingUpdate(...). Sets the thresholds indicating the magnitude of 
			changes occurring in any block that would be considered significant
			for re-meshing.

			@param[in] maxDistanceChangeThreshold: If the maximum change in a 
			block exceeds this value, then the block will be re-meshed. Setting
			the value to zero will retrieve all blocks.
			
			@param[in] avgDistanceChange: If the average change in a block 
			exceeds this value, then the block will be re-meshed. 
			Setting the value to zero will retrieve all blocks.

			@returns: PXC_STATUS_NO_ERROR, on success otherwise returns error code.
		*/
		virtual pxcStatus PXCAPI SetMeshingThresholds(const pxcF32 maxDistanceChangeThreshold, const pxcF32 avgDistanceChange) = 0;
		
		/**
		    @brief: Allows user to allocate PXCBlockMeshingData which can be 
			passed to DoMeshingUpdate. It's user's responsibility to explicitly 
			release the memory by calling Release().
		    
			@param[in] maxBlockMesh: Maximum number of mesh blocks client can 
			handle in one update from DoMeshingUpdate(...), If non-positive value is
			passed then it uses the default value. Use 
			PXCBlockMeshingData::QueryMaxNumberOfBlockMeshes() to check the value.
		    
			@param[in] maxFaces: Maximum number of faces that client can handle
			in one update from DoMeshingUpdate(...), If non-positive value is passed 
			then it uses the default value. Use 
			PXCBlockMeshingData::QueryMaxNumberOfFaces() to check the value.

		    @param[in] maxVertices: Maximum number of vertices that client can 
			handle in one update from DoMeshingUpdate(...). If non-positive value is
			passed then it uses the default value. Use 
			PXCBlockMeshingData::QueryMaxNumberOfVertices() to check the value.

		    @param[in] bUseColor: Flag indicating whether user wants 
			scene perception to return color per vertex in the mesh update. If 
			set the color buffer will be created in PXCBlockMeshingData 
			otherwise color buffer will not be created and any calls made to 
			PXCBlockMeshingData::QueryVerticesColor() will return NULL.
		   
		    @returns: on success returns valid handle to the instance otherwise returns NULL.
		*/
		virtual PXCBlockMeshingData* PXCAPI CreatePXCBlockMeshingData(const pxcI32 maxBlockMesh, const pxcI32 maxVertices, const pxcI32 maxFaces, const pxcBool bUseColor) = 0;
		
		__inline PXCBlockMeshingData* CreatePXCBlockMeshingData(const pxcI32 maxBlockMesh, const pxcI32 maxVertices, const pxcI32 maxFaces)
		{
			return CreatePXCBlockMeshingData(maxBlockMesh, maxFaces, maxVertices, 1);
		}
		__inline PXCBlockMeshingData* CreatePXCBlockMeshingData()
		{
			return CreatePXCBlockMeshingData(-1, -1, -1, 1);
		}
		
		/**
			@brief: Performs meshing and hole filling if requested. This 
			function can be slow if there is a lot of data to be meshed. For
			efficiency reason we recommend running this function on a separate
			thread. This call is designed to be thread safe if called in 
			parallel with ProcessImageAsync. 

			@param[in] blockMeshingData: Instance of pre-allocated 
			PXCBlockMeshingData. Refer to
			PXCScenePerception::CreatePXCBlockMeshingData(...) for how to allocate 
			PXCBlockMeshingData.
		
			@param[in] fillHoles: Argument to indicate whether to fill holes in
			mesh blocks. If set, it will fill missing details in each mesh block 
			that is visible from scene perception's camera current pose and 
			completely surrounded by closed surface(holes) by smooth linear 
			interpolation of adjacent mesh data.

			@param[in] meshingUpdateInfo: Argument to indicate which mesh 
			data you wish to use.
			   -countOfBlockMeshesRequired: If set, on successful call 
			    this function will set number of block meshes available for 
			    meshing which can be retrieved using QueryNumberOfBlockMeshes().

			   -blockMeshesRequired: Can only be set to true if 
				countOfBlockMeshesRequired is set to true otherwise the value is 
				ignored, If set to true, on successful call to this function it 
				will update block meshes array in pBlockMeshingUpdateInfo which can
				be retrieved using QueryBlockMeshes().

			   -countOfVeticesRequired: If set, on successful call to this 
				function it will set number of vertices available for meshing 
				which can be retrieved using QueryNumberOfVertices().

			   -verticesRequired: Can only be set if 
				countOfVeticesRequired is set to true otherwise the value is ignored, 
				If set, on successful call to this function it will update vertices
				array in pBlockMeshingUpdateInfo which can be retrieved using 
				QueryVertices().

			   -countOfFacesRequired: If set, on successful call to this 
			    function it will set number of faces available for meshing which 
			    can be retrieved using QueryNumberOfFaces().

			   -facesRequired: Can only be set, If countOfFacesRequired 
			    is set to true otherwise the value is ignored, If set, on 
			    successful call to this function it will update faces array in 
			    pBlockMeshingUpdateInfo which can be retrieved using QueryFaces().
	
				-colorsRequired: If set and PXCBlockMeshingData was created with color, on 
				 success function will fill in colors array which can be accessed using 
				 QueryVerticesColor().
			
			+NOTE: Set meshing thresholds to (0, 0) prior to calling DoMeshingUpdate
			with hole filling enabled to fill mesh regions that are not changed.

			@returns: On success PXC_STATUS_NO_ERROR otherwise error code will 
			be returned.
		*/
		virtual pxcStatus PXCAPI DoMeshingUpdate(PXCBlockMeshingData *blockMeshingData, const pxcBool bFillHoles, MeshingUpdateInfo* meshingUpdateInfo) = 0;

		__inline pxcStatus DoMeshingUpdate(PXCBlockMeshingData *blockMeshingData, const pxcBool bFillHoles)
		{
			return DoMeshingUpdate(blockMeshingData, bFillHoles, 0);
		}

		__inline pxcStatus DoMeshingUpdate(PXCBlockMeshingData *blockMeshingData)
		{
			return DoMeshingUpdate(blockMeshingData, 0);
		}
		
		/**
			@brief: Allows users to save mesh in an ASCII obj file in 
			MeshResolution::HIGH_RESOLUTION_MESH.
	
			@param[in] pFile: the path of the file to use for saving the mesh.
		
			@param[in] bFillHoles: Indicates whether to fill holes in mesh 
			before saving the mesh.

			@returns: On success PXC_STATUS_NO_ ERROR, Otherwise error code is 
			returned on failure.
		*/
		virtual pxcStatus PXCAPI SaveMesh(const pxcCHAR *pFile, const pxcBool bFillHoles) = 0;
		__inline pxcStatus SaveMesh(const pxcCHAR *pFile)
		{ 
			return SaveMesh(pFile, 0); 
		}

		/** 
			@brief: Allows user to check whether the input stream is suitable 
			for starting, resetting/restarting or tracking scene perception. 
			
			@param[in] PXCCapture::Sample: Input stream sample required by 
			scene perception module.

			@returns: Returns positive values between 0.0 and 1.0 to indicate how good is scene for 
					  starting, tracking or resetting scene perception.
		              1.0 -> represents ideal scene for starting scene perception.
					  0.0 -> represents unsuitable scene for starting scene perception.

					  Returns negative values to indicate potential reason for tracking failure
				     -1.0 -> represents a scene without enough structure/geomtery
					 -2.0 -> represents a scene without enough depth pixels 
							 (Too far or too close to the target scene or 
							  outside the range of depth camera)
					  Also, value 0.0 is returned when an invalid argument is passed or 
					  if the function is called before calling PXCSenseManager::Init().
		*/
		virtual pxcF32 PXCAPI CheckSceneQuality(PXCCapture::Sample *sample) = 0;

		/** 
		    @brief: Fills holes in the supplied depth image.

			@param[in] pDepthImage: Instance of depth image to be filled. 
			Pixels with depth value equal to zero will be linearly interpolated 
			with adjacent depth pixels. The image resolution should be 320X240.

			@returns: On success PXC_STATUS_NO_ERROR,
			Otherwise error code will be returned on failure.
		*/
		virtual pxcStatus PXCAPI FillDepthImage(PXCImage *pDepthImage) = 0;

		/**
			@brief: Allows user to access normals of surface that are within
			view from the camera's current pose.

			@param[out] normals: Array of pre-allocated PXCPoint3DF32 to store 
			normal vectors. Each normal vector has three components namely x, y 
			and z. The size in pixels must be QVGA and hence the array size in 
			bytes should be: (PXCPoint3DF32's byte size) x (320 x 240).

			@returns: On success PXC_STATUS_NO_ERROR,			
			Otherwise error code will be returned on failure.
		*/
		virtual pxcStatus PXCAPI GetNormals(PXCPoint3DF32* normals) = 0;

		/**
			@brief: Allows user to access the surface's vertices
		    that are within view from camera's current pose.
		
			@param[out] vertices: Array of pre-allocated PXCPoint3DF32 to store
			vertices. Each element is a vector of x, y and z components. The 
			image size in pixels must be QVGA and hence the array
		    size in bytes should be: (PXCPoint3DF32's byte size) x (320 x 240).
		
			@returns: On success PXC_STATUS_NO_ERROR,			
			Otherwise error code will be returned on failure.
		*/
		virtual pxcStatus PXCAPI GetVertices(PXCPoint3DF32 *vertices) = 0;

		/**
		    @brief: Allows user to save the current scene perception's state to
			a file and later supply the file to LoadState() to restore scene 
			perception to the saved state.
		
			@param[in] fileName: The path of the file to use for saving the 
		    scene perception state.
		
			@returns: On success PXC_STATUS_NO_ERROR,			
			Otherwise error code will be returned on failure.
		*/
		virtual pxcStatus PXCAPI SaveCurrentState(const pxcCHAR* fileName) = 0;

		/**
		    @brief: Allows user to load the current scene perception's state 
			from the file that has been created using SaveCurrentState. 
			This function is only available before calling 
			PXCSenseManager::Init().
			
			@param[in] fileName: The path of the file to load scene perception 
			state from.
			
			@returns: On success PXC_STATUS_NO_ERROR,			
			Otherwise error code will be returned on failure.
		*/

		virtual pxcStatus PXCAPI LoadState(const pxcCHAR* filename) = 0;

		/**
			@brief: Allows user to allocate CreatePXCSurfaceVoxelsData which can be 
			passed to ExportSurfaceVoxels. It's user's responsibility to explicitly 
			release the instance by calling Release().

			@param[in] voxelCount: Maximum number of voxels 
			client is expecting in each call to ExportSurfaceVoxels(...).
		    
			@param[in] bUseColor: Flag indicating whether user wants 
			scene perception to return color per voxel when ExportSurfaceVoxels(...) is 
			called. If set the color buffer will be allocated in PXCSurfaceVoxelsData 
			otherwise Color Buffer will not be created and any calls made to 
			PXCSurfaceVoxelsData::QuerySurfaceVoxelsColor() will return NULL.

			@returns: on success returns valid handle to the instance otherwise returns NULL.
		*/
		virtual PXCSurfaceVoxelsData* PXCAPI CreatePXCSurfaceVoxelsData(const pxcI32 voxelCount, const pxcBool bUseColor) = 0;
		/**
			@brief: Allows user to allocate CreatePXCSurfaceVoxelsData without color,
			which can be passed to ExportSurfaceVoxels with default estimate of number of voxels. 
			It's users responsibility to explicitly release the instance by calling Release().
		*/
		__inline PXCSurfaceVoxelsData* CreatePXCSurfaceVoxelsData()
		{
			return  CreatePXCSurfaceVoxelsData(-1, 0);
		}

		/**
			@brief: Allows user to export the centers of the voxels intersected
		    by the surface scanned. Optionally allows to specify region of interest for 
			surface voxels to be exported. Voxels will be exported in parts over 
			multiple calls to this function. Client is expected to check return code to 
			determine if all voxels are exported successfully or not.
		   
		    @param[out] surfaceVoxelsData: Pre-allocated instance of PXCSurfaceVoxelsData 
			using CreatePXCSurfaceVoxelsData method. On success the function will fill 
			in center of each surface voxel in an array which can be obtained using QueryCenterOfSurfaceVoxels
			and number of voxels which can be retrieved using QueryNumberOfSurfaceVoxels().

			@param[in] lowerLeftFrontPoint: Optional, PXCPoint3DF32 represents lower 
			left corner of the front face of the bounding box which specifies region of interest for exporting 
			surface voxels.

			@param[in] upperRightRearPoint: Optional, PXCPoint3DF32 represents upper 
			right corner of the rear face of the bounding box which specifies region of interest for exporting 
			surface voxels .
		   
		    @returns: If scene perception module is able to export all the surface 
		    voxels it has acquired it will return PXC_STATUS_NO_ERROR and after that 
		    any calls made to ExportSurfaceVoxels will restart exporting all the
		    voxels again.
		    If all voxels cannot be fit into specified surfaceVoxelsData, it will 
		    return warning code PXC_STATUS_DATA_PENDING indicating that client 
		    should make additional calls to ExportSurfaceVoxels to get remaining 
		    voxels until PXC_STATUS_NO_ERROR is returned.
		*/
		virtual pxcStatus PXCAPI ExportSurfaceVoxels(PXCSurfaceVoxelsData* surfaceVoxelsData, const PXCPoint3DF32 *lowerLeftFrontPoint, const PXCPoint3DF32 *upperRightRearPoint) = 0;
		
		/**
			@brief: Allows to Export Surface Voxels present in the entire volume.
		*/
		__inline pxcStatus ExportSurfaceVoxels(PXCSurfaceVoxelsData* surfaceVoxelsData)
		{
			return ExportSurfaceVoxels(surfaceVoxelsData, 0, 0);
		}

		/**
			@brief: Allows user to get information regarding the planar 
			surfaces in the scene.

			@param[in] sample: Input stream sample required by scene perception module.

			@param[in] minPlaneArea: Minimum plane area to be detected in physical 
			dimension (m^2). This parameter refers to the physical size of the
			frontal planar surface at 1 meter to 3 meter from the camera. It controls 
			the threshold for the number of planes to be returned. Setting it to a
			smaller value makes the function to return smaller planes as well.
			E.g 0.213X0.213 m^2. The maximum acceptable value is 16.

			@param [in] maxPlaneNumber: Maximum number of planes that user wishes
			to detect, It should also match number of rows of the equation 
			array pPlaneEq.

			@param[out] pPlaneEq: Pre-allocated float array for storing the 
			plane equations detected by the function. Each row contains the 
			coefficients {a,b,c,w} of the detected plane, Hence the number of 
			rows are equal to maxPlaneNumber. a, b, c are co-efficients of 
			normalized plane equation and w is in meters.
			E.g. Row 0 of pPlaneEq will contain the plane equation: ax+by+cz+w
			in the form	pPlaneEq[0][0] to pPlaneEq[0][3] = {a,b,c,w}. Similarly
			rest of the rows will provide the equations for the remaining 
			planes. Rows for which planes are not detected will have all values 0.
			
			@param[out] pPlaneIndexImg: Pre-allocated array of (320X240) to store plane 
			ids. On success each index will have one of the following values 
				- 0: If the pixel is not part of any detected planes 
				- 1: If the pixel is part of the first plane detected
				- 2: If the pixel is part of the second plane is detected
				and so on.

			@returns: On success PXC_STATUS_NO_ERROR,			
			Otherwise error code will be returned on failure.
		*/
		virtual pxcStatus PXCAPI ExtractPlanes(PXCCapture::Sample *sample, pxcF32 minPlaneArea, pxcI32 maxPlaneNumber,
											   pxcF32 pPlaneEq[][4], pxcBYTE *pPlaneIndexImg) = 0;
		
		/**
			@brief: Allows user to set meshing resolution for DoMeshingUpdate(...).

			@param[in] meshResolution: Mesh Resolution user wishes to set.

			@returns: On success PXC_STATUS_NO_ERROR,			
			Otherwise error code will be returned on failure.
		*/
		virtual pxcStatus PXCAPI SetMeshingResolution(MeshResolution meshResolution) = 0;

		/**
		    @brief: Allows user to get Meshing resolution used by DoMeshingUpdate(...).
		
		    @returns: MeshResolution used by DoMeshingUpdate(...).
		*/
		virtual MeshResolution PXCAPI QueryMeshingResolution() = 0;

		/**
		    @brief: Allows user to get meshing thresholds used by scene 
			perception.

			@param[out] maxDistanceChangeThreshold: Pre-allocated pxcF32 where 
			user wishes to retrieve max distance change threshold.

			@param[out] avgDistanceChange: Pre-allocated pxcF32 where 
			user wishes to retrieve average distance change threshold.

			@returns: On success PXC_STATUS_NO_ERROR,			
			Otherwise error code will be returned on failure.
		*/
		virtual pxcStatus PXCAPI QueryMeshingThresholds(pxcF32 *maxDistanceChangeThreshold, pxcF32 *avgDistanceChange) = 0;

		/** 
			@brief: Allows user to set region of interest for Meshing. If used,
			The DoMeshingUpdate(...) function will only 
			mesh these specified regions. Use ClearMeshingRegion() to clear the 
			meshing region set by this function.

			@param[in] lowerLeftFrontPoint: Pre-allocated PXCPoint3DF32 which 
			specifies lower left corner of the front face of the bounding box.
		
			@param[in] upperRightRearPoint: Pre-allocated PXCPoint3DF32 which specifies 
			upper right corner of the rear face of the bounding box.

			@returns: On success PXC_STATUS_NO_ERROR,			
			Otherwise error code will be returned on failure.
		*/
		virtual pxcStatus PXCAPI SetMeshingRegion(const PXCPoint3DF32 *lowerLeftFrontPoint, const PXCPoint3DF32 *upperRightRearPoint) = 0;

		/**
			@brief: Allows user to clear meshing region set by SetMeshingRegion(...).

			@returns: On success PXC_STATUS_NO_ERROR,			
				Otherwise error code will be returned on failure.
		*/
		virtual pxcStatus PXCAPI ClearMeshingRegion() = 0;

		/**
			@brief: Allows user to enforce the supplied pose as the camera pose 
			.The module will track the camera from this pose when the next 
			frame is passed. This function can be called any time after module finishes 
			processing first frame or any time after module successfully processes the 
			first frame post a call to Reset scene perception.
			
			@param[in] pose: Array of 12 pxcF32 that stores the camera pose
			user wishes to set in row-major order. Camera pose is specified in a 
			3 by 4 matrix [R | T] = [Rotation Matrix | Translation Vector]
			where R = [ r11 r12 r13 ]
			          [ r21 r22 r23 ] 
			          [ r31 r32 r33 ]
			      T = [ tx  ty  tz  ]
			Pose Array Layout = [r11 r12 r13 tx r21 r22 r23 ty r31 r32 r33 tz]
			Translation vector is in meters.
		
			@returns: PXC_STATUS_NO_ERROR, if the function succeeds.
			Otherwise error code will be returned.
		*/
		virtual pxcStatus PXCAPI SetCameraPose(const pxcF32 pose[12]) = 0;

		/**
			@brief: Allows user to get length of side of voxel cube in meters.

			@returns: Returns length of side of voxel cube in meters.
		*/
		virtual pxcF32 PXCAPI QueryVoxelSize() = 0;

		/**
			@brief: Allows user to get the intrinsics of internal scene perception 
			camera.	These intrinsics should be used with output images obtained from 
			the module. Such as QueryVolumePreview(...), GetVertices(...) and 
			GetNormals(..). This function should only be used after calling 
			PXCSenseManager::Init() otherwise it would return an error code.

			@param[out] spIntrinsics: Handle to pre-allocated instance of 
			ScenePerceptionIntrinsics. On success this instance will be filled with 
			appropriate values.

			@returns: PXC_STATUS_NO_ERROR, if the function succeeds.
			Otherwise error code will be returned.
		*/
		virtual pxcStatus PXCAPI GetInternalCameraIntrinsics(ScenePerceptionIntrinsics *spIntrinsics) = 0;

		/**
			@brief: Allows user to integrate specified stream from supplied pose in to 
			the reconstructed volume.

			@param[in] sample: Input stream sample required by scene perception module.
			Obtained using PXCSenseManager::QueryScenePerceptionSample().

			@param[in] pose: Estimated pose for the supplied input stream.
			Array of 12 pxcF32 that stores the camera pose
			user wishes to set in row-major order. Camera pose is specified in a 
			3 by 4 matrix [R | T] = [Rotation Matrix | Translation Vector]
			where R = [ r11 r12 r13 ]
					  [ r21 r22 r23 ] 
			          [ r31 r32 r33 ]
			      T = [ tx  ty  tz  ]
			Pose Array Layout = [r11 r12 r13 tx r21 r22 r23 ty r31 r32 r33 tz]
			Translation vector is in meters.

			@returns: PXC_STATUS_NO_ERROR, if the function succeeds.
			Otherwise error code will be returned.
		*/
		virtual pxcStatus PXCAPI DoReconstruction(PXCCapture::Sample *sample, const pxcF32 pose[12]) = 0;

		/**
			@brief: Allows user to enable/disable re-localization feature of scene 
			perception's camera tracking. By default re-localization is enabled. This 
			functionality is only available after PXCSenseManager::Init() is called.

			@param[in] enableRelocalization: Flag specifying whether to enable or 
			disable re-localization.
			
			@returns: PXC_STATUS_NO_ERROR, if the function succeeds.
			Otherwise error code will be returned.
		*/
		virtual pxcStatus PXCAPI EnableRelocalization(pxcBool enableRelocalization) = 0;

		/**
			@brief: Allows user to transform plane equations obtained from
			ExtractPlanes(...) to world co-ordinate system using the provided 
			pose and returns number of planes found in supplied plane equation.

			@param [in] maxPlaneNumber: Number of rows of the equation 
			array pPlaneEq.

			@param[in | out] pPlaneEq: Pre-allocated float array plane equations 
			obtained from  ExtractPlanes(...). On success the plane equations will 
			be transformed in to world coordinate system using supplied camera pose.

			@param[in] pose: Array of 12 pxcF32 that stores camera pose
			of the capture sample that was supplied to the ExtractPlanes(...).
			stored in row-major order. Camera pose is specified in a 
			3 by 4 matrix [R | T] = [Rotation Matrix | Translation Vector]
			where R = [ r11 r12 r13 ]
					  [ r21 r22 r23 ] 
					  [ r31 r32 r33 ]
				  T = [ tx  ty  tz  ]
			Pose Array Layout = [r11 r12 r13 tx r21 r22 r23 ty r31 r32 r33 tz]
			Translation vector is in meters.

			+NOTE: Use the pose obtained from GetCameraPose(...) 
			to transform plane equations.

			@returns: On success, returns positive number indicating 
			number of planes found in pPlaneEq. Negative number indicates errors like 
			invalid argument.
		*/
		virtual pxcI32 PXCAPI TransformPlaneEquationToWorld(pxcI32 maxPlaneNumber, pxcF32 pPlaneEq[][4], const pxcF32 pose[12]) = 0;

		typedef struct
		{
			pxcBool fillMeshHoles;
			pxcBool saveMeshColor;
			MeshResolution meshResolution;
		}SaveMeshInfo;

		/**
			@brief: Allows users to save different configuration of mesh in an 
			ASCII obj file.
	
			@param[in] filename: the path of the file to use for saving the mesh.
		
			@param[in] saveMeshInfo: Argument to indicate mesh configuration 
			you wish to save.

				-fillMeshHoles: Flag indicates whether to fill holes in saved mesh. 

				saveMeshColor: Flag indicates whether to save mesh with color.

				meshResolution: Indicates resolution for mesh to be saved.

			@returns: On success PXC_STATUS_NO_ ERROR, Otherwise error code is 
			returned on failure.
		*/

		virtual pxcStatus PXCAPI SaveMeshExtended(const pxcCHAR *filename, const SaveMeshInfo *saveMeshInfo) = 0;

		/**
			@brief: Allows user to enable or disable inertial sensor support for scene perception,
			by default it is disabled. This function is only available before calling 
			PXCSenseManager::Init().

			@returns: On success PXC_STATUS_NO_ ERROR, Otherwise error code is 
			returned on failure.
		*/
		virtual pxcStatus PXCAPI EnableInertialSensorSupport(const pxcBool enable) = 0;

		/**
			@brief: Allows user to enable or disable gravity sensor support for scene perception,
			by default it is enabled. This function is only available before calling 
			PXCSenseManager::Init().

			@returns: On success PXC_STATUS_NO_ ERROR, Otherwise error code is 
			returned on failure.
		*/
		virtual pxcStatus PXCAPI EnableGravitySensorSupport(const pxcBool enable) = 0;

		/**
			@brief: Allows user to get status(enabled/disabled) of gravity sensor support 
			for scene perception.

			@returns: On success PXC_STATUS_NO_ ERROR, Otherwise error code is 
			returned on failure.
		*/
		virtual pxcBool PXCAPI IsGravitySensorSupportEnabled() = 0;

		/**
			@brief: Allows user to get status(enabled/disabled) of inertial sensor support
			for scene perception.

			@returns: On success PXC_STATUS_NO_ ERROR, Otherwise error code is 
			returned on failure.
		*/
		virtual pxcBool PXCAPI IsInertialSensorSupportEnabled() = 0;

		/**
			@brief: Allows user to access volume details like 2D projection image of reconstructed 
			volume by ray-casting, surface volume normals and surface volume faces from a given camera pose

			@param[in] pose: Array of 12 pxcF32 that stores camera pose
			in row-major order. Camera pose is specified in a 
			3 by 4 matrix [R | T] = [Rotation Matrix | Translation Vector]
			where R = [ r11 r12 r13 ]
					  [ r21 r22 r23 ] 
					  [ r31 r32 r33 ]
				  T = [ tx  ty  tz  ]
			Pose Array Layout = [r11 r12 r13 tx r21 r22 r23 ty r31 r32 r33 tz]
			Translation vector is in meters.

			@param[out] volumeImageData: Optional pre-allocated pxcBYTE array of size in bytes:
			4 X ScenePerceptionIntrinsics.imageSize.width X ScenePerceptionIntrinsics.imageSize.height X (pxcBYTE's size)
			where user wishes to store the projection data of the volume. ScenePerceptionIntrinsics is obtained using 
			GetInternalCameraIntrinsics(...). It contains 4 channels per pixel in RGBA order.

			@param[out] vertices: Optional pre-allocated array of pxcF32 to store volume
			vertices. Each vertex is represented by 3 components x, y and z in order. Therefor 
			the array size in bytes should be: (pxcF32's byte size) X  
			ScenePerceptionIntrinsics.imageSize.width X ScenePerceptionIntrinsics.imageSize.height.
			Where ScenePerceptionIntrinsics is obtained using GetInternalCameraIntrinsics(...). 

			@param[out] normals: Optional pre-allocated array of pxcF32 to store 
			volume normals. Each normal is represented by 3 components x, y and z in order. 
			Therefor the array size in bytes should be: (pxcF32's byte size) X  
			ScenePerceptionIntrinsics.imageSize.width X ScenePerceptionIntrinsics.imageSize.height.
			Where ScenePerceptionIntrinsics is obtained using GetInternalCameraIntrinsics(...). 

			@returns: On success PXC_STATUS_NO_ ERROR, Otherwise error code is 
			returned on failure.
		*/
		virtual pxcStatus PXCAPI GetVolumePreview(const pxcF32 pose[12], pxcBYTE *volumeImageData, pxcF32* vertices, pxcF32* normals) = 0;
};
