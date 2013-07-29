function [K,option]=svmkernel(x,kernel,kerneloption,xsup,framematrix,vector,dual);

% Usage  K=svkernel(x,kernel,kerneloption,xsup,frame,vector,dual);
%
% Returns the scalar product of the vectors x by using the
% mapping defined by the kernel function or x and xsup
% if the matrix xsup is defined
%
% Input
% 
% x		:input vectors
% kernel 	: kernel function
%		Type								Function					Option
%		Polynomial						'poly'					Degree (<x,xsup>+1)^d
%		Homogeneous polynomial		'polyhomog'				Degree <x,xsup>^d	
%		Gaussian							'gaussian'				Bandwidth
%		Heavy Tailed RBF				'htrbf'					[a,b]   %see Chappelle 1999	
%		Mexican 1D Wavelet 			'wavelet'
%		Frame kernel					'frame'					'sin','numerical'...	
%
%  kerneloption	: scalar or vector containing the option for the kernel
% 'gaussian' : scalar gamma is identical for all coordinates
%              otherwise is a vector of length equal to the number of 
%              coordinate
% 
%
% 'poly' : kerneloption is a scalar given the degree of the polynomial
%          or is a vector which first element is the degree of the polynomial
%           and other elements gives the bandwidth of each dimension.
%          thus the vector is of size n+1 where n is the dimension of the problem.
%
%
% xsup		: support vector
%
% ----- 1D Frame Kernel -------------------------- 
%
%  framematrix  frame elements for frame kernel
%  vector       sampling position of frame elements
%	dual 		  dual frame
%  frame,vector and dual are respectively the matrices and the vector where the frame 
%  elements have been processed. these parameters are used only in case
%
%
%	see also svmreg,svmclass,svmval, kernelwavelet,kernelframe
%

% O4/O6/2000 A. Rakotomamonjy


if nargin < 6
    vector=[];
    dual=[];
end;
if nargin <5
    frame=[];
end;

if nargin<4
    xsup=x;
end;
if nargin<3
    kerneloption=1;
end;
if nargin<2
    kernel='gaussian';
end;
if isempty(xsup)
    xsup=x;
end;
[n1 n2]=size(x);
[n n3]=size(xsup);
ps  =  zeros(n1,n);			% produit scalaire
switch lower(kernel)
case 'poly'
    
    [nk,nk2]=size(kerneloption);   
    if nk>nk2
        kerneloption=kerneloption';
        nk2=nk;
    end;
    if nk2==1
        degree=kerneloption;
        var=ones(1,n2);
        
    elseif nk2 ==2
        degree=kerneloption(1);
        var=ones(1,n2)*kerneloption(2);
        
    elseif nk2== n2+1
        degree=kerneloption(1);
        var=kerneloption(2:n2+1);
        
    elseif nk2 ==n2+2
        degree=kerneloption(1);
        var=kerneloption(2:n2+1);
    end;

    if nk2==1
        aux=1;
    else
        aux=repmat(var,n,1);
    end;
  
    ps= x *(xsup.*aux.^2)';

    if degree > 1
        K =(ps+1).^degree;
    else
        K=ps;
    end;
case 'polyhomog'
    
    [nk,nk2]=size(kerneloption);   
    if nk>nk2
        kerneloption=kerneloption';
        nk2=nk;
    end;
    if nk2==1
        degree=kerneloption;
        var=ones(1,n2);
    else
        if nk2 ~=n2+1
            degree=kerneloption(1);
            var=ones(1,n2)*kerneloption(2);
        else
            degree=kerneloption(1);
            var=kerneloption(2:nk2);
        end;
    end;
    
    
    aux=repmat(var,n,1);
    ps= x *(xsup.*aux.^2)';
    K =(ps).^degree;
    
    
case 'gaussian'
    [nk,nk2]=size(kerneloption);
    if nk ~=nk2
        if nk>nk2
            kerneloption=kerneloption';
        end;
    else
        kerneloption=ones(1,n2)*kerneloption;
    end;
    
    if length(kerneloption)~=n2 & length(kerneloption)~=n2+1 
        error('Number of kerneloption is not compatible with data...');
    end;
    
    
    metric = diag(1./kerneloption.^2);
    ps = x*metric*xsup'; 
    [nps,pps]=size(ps);
    normx = sum(x.^2*metric,2);
    normxsup = sum(xsup.^2*metric,2);
    ps = -2*ps + repmat(normx,1,pps) + repmat(normxsup',nps,1) ; 
    
    
    K = exp(-ps/2);
    
case 'htrbf'    % heavy tailed RBF  %see Chappelle Paper%
    b=kerneloption(2);
    a=kerneloption(1);
    for i=1:n
        ps(:,i) = sum( abs((x.^a - ones(n1,1)*xsup(i,:).^a)).^b   ,2);
    end;
    
    
    K = exp(-ps);
    
case 'gaussianslow'    %
    %b=kerneloption(2);
    %a=kerneloption(1);
    for i=1:n
        ps(:,i) = sum( abs((x - ones(n1,1)*xsup(i,:))).^2 ,2)./kerneloption.^2/2;
    end;
    
    
    K = exp(-ps);
case 'multiquadric'
    metric = diag(1./kerneloption);
    ps = x*metric*xsup'; 
    [nps,pps]=size(ps);
    normx = sum(x.^2*metric,2);
    normxsup = sum(xsup.^2*metric,2);
    ps = -2*ps + repmat(normx,1,pps) + repmat(normxsup',nps,1) ; 
    K=sqrt(ps + 0.1);
case 'wavelet'
    K=kernelwavelet(x,kerneloption,xsup);     
case 'frame'
    K=kernelframe(x,kerneloption,xsup,framematrix,vector,dual);
case 'wavelet2d'
    K=wav2dkernelint(x,xsup,kerneloption);
case 'radialwavelet2d'
    K=radialwavkernel(x,xsup);    
case 'tensorwavkernel'
    [K,option]=tensorwavkernel(x,xsup,kerneloption);  

case 'numerical'
    K=kerneloption.matrix;
case 'polymetric'
    K=x*kerneloption.metric*xsup';
    
case 'jcb'
    K=x*xsup';
    
end;



