function varargout=OVRtrain_v6(dataset,m)
%----------------------------------------------------------
% EEGStarlab Toolbox
% (C) Starlab S.L.
%----------------------------------------------------------
% this function is intended to extract the Common Spatial Patterns for a
% multiclass situation
%
% [see paper One Versus the Rest (OVR) algorithm: an extension of common 
% spatial patterns (CSP) algorithm to multi-class case by Wi, Gao and Gao]
%----------------------------------------------------------
%Inputs
% dataset:  cell_array{} with length equal to the number of classes. in each
%            cell there is a matrix of dimensions S X F. S= samplesOfEpoch*numberOfEpochs, i.e. it results from 
%           concatenating all epochs in a vector. F is the feature dimension: in case of using EEG channels 60, if 
%           Morlet wavelet projection is used, F=1800. This is the
%           dimensions of input space.
% m:        4 dim vector with the number of indicates the number of eigenvector to
%           be taken into account for the
%           projection of the data for each class, i.e. number of
%           dimensions of the output space
%----------------------------------------------------------
%Outputs
% extractor: a feature_extract_matrix for each class, all ordered in cell
% array of length number of classes.
% 		each cell containing a matrix of size m(i) x F, i.e. m is different
% 		for each class.
% fe_dim:	array with the number of eigenvalues used for the projection for each
%		class (dimension of the new feature space). This is eq  ual to
%		vector m
% dataset_filt: projection of the training data through the OVR in the new
% feature space of the same class, it is computed as
% dataset_filt{i}=extractor{i}*dataset{i}';
%----------------------------------------------------------
%Dependencies
%
%----------------------------------------------------------
% Version   Date        Author  Changes 
% v1        21/10/08    JLL     -First version based on matlab decimate function 
% v2        05/05/09    ICL     -introduced input m to select the number of
%                               eigenvalues to pe taken into acount in the projection
% v3        14/08/09    ICL     -Change the way of obtainning  the Whitenning
%                               matrix.
%                               -The data matrix was transposed: corrected
% v4        18/08/09    ICL     -we change the extractor. 
%                               Old: extractor{i}=SP{i}*SF{i};
%                               New  extractor{i}=SF{i}; The input data now
%                               has to be transposed and there is a
%                               dimensionality reduction of the features.
% v5        25/08/09    ICL     -the number of eigenvalues selected for
%                               each class is now an input parameter.
% v6        12/07/2010  ICL     -the data matrix to calculate the
%                               covariance r is slpited into parts to avoid
%                               out of memory problems when the
%                               dimensionality of the features is very high
%----------------------------------------------------------
% EX.
% 
% [extractor,fe_dim,dataset_filt]=OVRtrain(dataset,m)
% OR
% extractor=OVRtrain(dataset,m)
%--------------------------------------------------------------

if nargin<2
    tolerance=0.1;
    m=0.85;
end


%1. estimate covariance matrix for each condition
space_dim=length(dataset{1}(1,:));
for i=1:length(dataset) %for each class.
    r_temp=sparse(space_dim,space_dim);
    for part=1:10000:length(dataset{i}(:,1))%we divide the matrix in parts to avoid memory errors
        if part+10000<=length(dataset{i}(:,1))
            data= dataset{i}(part:part+10000,:);
            r_temp=r_temp+(data'*data);
        elseif part+10000>length(dataset{i}(:,1))
            data= dataset{i}(part:end,:);
            r_temp=r_temp+(data'*data);
        end
    end
    r{i}=r_temp;
    dataset{i}=[];%we clean it to save memory, this should be commented if we want 
    clear data r_temp
    pack
end
clear dataset

r_all=zeros(size(r{1}));
for i=1:length(r)
    r_all=r_all+r{i};
end

%2. construct whitening matrix

[U,D]=eig(r_all);
W=D^(-1/2)*U';


%3, for each i
 for i=1:length(r)
    %4.find s{i} from r{i} and factorize
    s{i}=W*r{i}*W';
    sall=W*r_all*W';
    [Us{i},Ds{i}]=eig(s{i},sall);
    %5. select m principal component
    %NOTE This is the trickiest part.
    
    %5.a we find the elements "close" to 1.
  
    vaps=diag(Ds{i});
%    temp=find(vaps>=m*max(vaps));
    [Max,I]=sort(vaps,'descend');
    ind{i}=I(1:m(i));
% temp=find(vaps>=m*max(vaps));
% ind{i}=temp;
    %5.b we take their eigenvectors as principal components of that class:
    Uss{i}=Us{i}(:,ind{i});
    %6. so, now we build the spatial filter:
    SF{i}=Uss{i}'*W;
    
    %7. the signal components corresponding to the class back in the
    %dataset space:
    %7.a we find the spatial pattern matrix, the pseudo-inverse of the
    %spatial filter
   
    %although we need to find the pseudo-inverse, it gives problems
    %because of tolerance, so instead of:
    %SP{i}=inv(SF{i}'*SF{i})*SF{i}';
     %we use the Penrose-Moore matrix:
     SP{i}=pinv(SF{i});
    %7.b  we "filter" in space
    extractor{i}=SF{i};
end
varargout{1}=extractor;

if nargout>2
    %just in case aditional info is useful:
    fe_dim=[];

    for i=1:length(SF)
        this_sf=SF{i};     %this should ensure its a "row", or have fe_dim rows
        dataset_filt{i}=extractor{i}*dataset{i}';  
        fe_dim=[fe_dim, length(ind{i})];
    end
    varargout{2}=fe_dim;
    varargout{3}=dataset_filt;
end
% %basic test:
% % data_test={normrnd(0.3,0.7,300,1),normrnd(-1.7,0.3,300,1),normrnd(-0.2,0.2,300,1)+normrnd(0.7,0.3,300,1)};
% % [fe_vector,fe_dim,dataset_filt]=OVRtrain(data_test)
%     
%     
% %more elaborated test:(similar to section III of same article)
% % s2=sin(2*3.1415*0.06.*[1:500]);
% % s4=1*sin(2*3.1415*0.1.*[1:500])+2*sin(2*3.1415*0.2.*[1:500])+7*sin(2*3.1415*0.28.*[1:500]);
% % s6=normrnd(0,0.8,1,500);
% % s5=sign(s6);
% % 
% % c2=normrnd(0,1,40,1);
% % c4=normrnd(0,1,40,1);
% % c5=normrnd(0,1,40,1);
% % c6=normrnd(0,1,40,1);
% % 
% % xb=[c2 c5 c6 ]*[s2; s5; s6];
% % xd=[c4 c5 c6]*[s4; s5; s6];
% %data_train={xb,xd};
% %
% %[extractor,fe_dim,dataset_filt]=OVRtrain_v2(data_train);
% %
% %
% %we plot the original signal, the mixed signal and the result of filtering
% %with OVR
% % 
% % the_channel=3;
% % 
% % filt1=dataset_filt{1}(the_channel,:);
% % train1=data_train{1}(the_channel,:);
% % orig1=(c2*s2);
% % orig1=orig1(the_channel,:);
% % 
% % %we mean the energy:
% % %filt1=filt1./(filt1*filt1');
% % %train1=train1./(train1*train1');
% % %orig1=orig1./(orig1*orig1');
% % 
% % figure
% % subplot 311
% % 
% % plot(orig1,'r');
% % title('original independent component, input signal, and output signal')
% % subplot 312
% % plot(train1,'y')
% % subplot 313
% % plot(filt1,'b');
% % hold on
% % plot(orig1,'r--');
