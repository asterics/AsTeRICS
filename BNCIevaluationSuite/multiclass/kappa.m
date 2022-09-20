function [kap,se,H,zscore,p0,SA,R]=kappa(d,c,kk);
% KAPPA.M estimates Cohen's kappa coefficient 
%
% [kap,sd,H,z,OA,SA,MI] = kappa(d1,d2);
% [kap,sd,H,z,OA,SA,MI] = kappa(H);
%
% d1    data of scorer 1 
% d2    data of scorer 2 
%
% kap	Cohen's kappa coefficient point
% se	standard error of the kappa estimate
% H	data scheme (Concordance matrix or confusion matrix)
% z	z-score
% OA	overall agreement 
% SA	specific agreement 
% MI 	Mutual information or transfer information (in [bits])
%
% Reference(s):
% [1] Cohen, J. (1960). A coefficient of agreement for nominal scales. Educational and Psychological Measurement, 20, 37-46.
% [2] J Bortz, GA Lienert (1998) Kurzgefasste Statistik f|r die klassische Forschung, Springer Berlin - Heidelberg. 
%        Kapitel 6: Uebereinstimmungsmasze fuer subjektive Merkmalsurteile. p. 265-270.
% [3] http://www.cmis.csiro.au/Fiona.Evans/personal/msc/html/chapter3.html
% [4] Kraemer, H. C. (1982). Kappa coefficient. In S. Kotz and N. L. Johnson (Eds.), 
%        Encyclopedia of Statistical Sciences. New York: John Wiley & Sons.
% [5] http://ourworld.compuserve.com/homepages/jsuebersax/kappa.htm
%
%  

%	$Revision: 1.7 $
%	$Id: kappa.m,v 1.7 2006/04/25 10:31:42 schloegl Exp $
%	Copyright (c) 1997-2006 by Alois Schloegl <a.schloegl@ieee.org>	
%    	This is part of the BIOSIG-toolbox http://biosig.sf.net/

% This library is free software; you can redistribute it and/or
% modify it under the terms of the GNU Library General Public
% License as published by the Free Software Foundation; either
% version 2 of the License, or (at your option) any later version.
%
% This library is distributed in the hope that it will be useful,
% but WITHOUT ANY WARRANTY; without even the implied warranty of
% MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
% Library General Public License for more details.
%
% You should have received a copy of the GNU Library General Public
% License along with this library; if not, write to the
% Free Software Foundation, Inc., 59 Temple Place - Suite 330,
% Boston, MA  02111-1307, USA.
%

if nargin>1,
	if (isa(d,'double') | isa(d,'single')) & any(rem(d,1)) 
        	fprintf(2,'Error %s: class information is not integer\n',mfilename);
		return;
	end;
	if (isa(c,'double') | isa(c,'single')) & any(rem(c,1)) 
        	fprintf(2,'Error %s: class information is not integer\n',mfilename);
		return;
	end;
        
        [dr,dc] = size(d);
    	[cr,cc] = size(c);

    	N  = min(cr,dr); % number of examples
    	ku = max([d;c]); % upper range
    	kl = min([d;c]); % lower range
    
	
    	if (nargin<3),
            	d = d-kl+1;	% minimum element is 1;
            	c = c-kl+1;	%
            	kk= ku-kl+1;  	% maximum element
    	else
            	if kk<ku;  	% maximum element
                    	fprintf(2,'Error KAPPA: some element is larger than arg3(%i)\n',kk);
            	end;
    	end;
    
    	if 0,
        	h = histo([d+c*kk; kk*kk+1; 1]); 
        	H = reshape(h(1:length(h)-1));
        	H(1,1) = H(1,1)-1;
    	else
		if 1;   % exist('OCTAVE_VERSION')>=5;
	        	H = zeros(kk);
    			for k = 1:N, 
    				if ~isnan(d(k)) & ~isnan(c(k)),
		    			H(d(k),c(k)) = H(d(k),c(k)) + 1;
		    		end;	
        		end;
		else
			H = full(sparse(d(1:N),c(1:N),1,kk,kk));
    		end;
	end;
else
	tmp = min(size(d));
    	H = d(1:tmp,1:tmp);
    	% if size(H,1)==size(H,2);	
	N = sum(sum(H));
    	% end;
end;

warning('off');
p0  = sum(diag(H))/N;  %accuracy of observed agreement, overall agreement 
%OA = sum(diag(H))/N);

p_i = sum(H); %sum(H,1);
pi_ = sum(H'); %sum(H,2)';

SA  = 2*diag(H)'./(p_i+pi_); % specific agreement 

pe  = (p_i*pi_')/(N*N);  % estimate of change agreement

px  = sum(p_i.*pi_.*(p_i+pi_))/(N*N*N);

%standard error 
kap = (p0-pe)/(1-pe);
sd  = sqrt((pe+pe*pe-px)/(N*(1-pe*pe)));

%standard error 
se  = sqrt((p0+pe*pe-px)/N)/(1-pe);
zscore = kap/se;

if nargout<7, return; end; 

% Nykopp's entropy
pwi = sum(H,2)/N;                       % p(x_i)
pwj = sum(H,1)/N;                       % p(y_j)
pji = H./repmat(sum(H,2),1,size(H,2));  % p(y_j | x_i) 
pwj(pwj==0) = 1;                        % make sure p*log2(p) is 0, this avoids NaN's 
pji(pji==0) = 1;                        % make sure p*log2(p) is 0, this avoids NaN's 
R   = - sum(pwj.*log2(pwj)) + sum(pwi'*(pji.*log2(pji)));



