function coeff=coeffBin2coeffFloat(coeffBin,nBits,scaleFactor)
    nCoefs=length(coeffBin)/nBits;
    for i=1:nCoefs
        coeff(:,i)=bin2dec(num2str(coeffBin(:,(i-1)*nBits+1:(i-1)*nBits+nBits)))/(2^nBits);
    end
    coeff=(scaleFactor(2)-scaleFactor(1))*coeff+scaleFactor(1);