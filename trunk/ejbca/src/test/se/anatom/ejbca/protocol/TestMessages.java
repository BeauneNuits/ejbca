/*************************************************************************
 *                                                                       *
 *  EJBCA: The OpenSource Certificate Authority                          *
 *                                                                       *
 *  This software is free software; you can redistribute it and/or       *
 *  modify it under the terms of the GNU Lesser General Public           *
 *  License as published by the Free Software Foundation; either         *
 *  version 2.1 of the License, or any later version.                    *
 *                                                                       *
 *  See terms of license at gnu.org.                                     *
 *                                                                       *
 *************************************************************************/

package se.anatom.ejbca.protocol;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import junit.framework.TestCase;
import org.apache.log4j.Logger;
import se.anatom.ejbca.util.Base64;
import se.anatom.ejbca.util.CertTools;
import se.anatom.ejbca.util.KeyTools;


/**
 * Tests signing session.
 *
 * @version $Id: TestMessages.java,v 1.1 2004-06-10 16:17:44 sbailliez Exp $
 */
public class TestMessages extends TestCase {
    static byte[] keytoolp10 = Base64.decode(("MIIBbDCB1gIBADAtMQ0wCwYDVQQDEwRUZXN0MQ8wDQYDVQQKEwZBbmFUb20xCzAJBgNVBAYTAlNF" +
            "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDY+ATE4ZB0oKfmXStu8J+do0GhTag6rOGtoydI" +
            "eNX9DdytlsmXDyONKl8746478/3HXdx9rA0RevUizKSataMpDsb3TjprRjzBTvYPZSIfzko6s8g6" +
            "AZLO07xCFOoDmyRzb9k/KEZsMls0ujx79CQ9p5K4rg2ksjmDeW7DaPMphQIDAQABoAAwDQYJKoZI" +
            "hvcNAQEFBQADgYEAyJVobqn6wGRoEsdHxjoqPXw8fLrQyBGEwXccnVpI4kv9iIZ45Xres0LrOwtS" +
            "kFLbpn0guEzhxPBbL6mhhmDDE4hbbHJp1Kh6gZ4Bmbb5FrwpvUyrSjTIwwRC7GAT00A1kOjl9jCC" +
            "XCfJkJH2QleCy7eKANq+DDTXzpEOvL/UqN0=").getBytes());
    static byte[] oldbcp10 = Base64.decode(("MIIBbDCB1gIBADAtMQswCQYDVQQGEwJTRTEPMA0GA1UEChMGQW5hVG9tMQ0wCwYDVQQDEwRUZXN0" +
            "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCzN9nDdwmq23/RLGisvR3CRO9JSem2QZ7JC7nr" +
            "NlbxQBLVqlkypT/lxMMur+lTX1S+jBaqXjtirhZTVaV5C/+HObWZ5vrj30lmsCdgzFybSzVxBz0l" +
            "XC0UEDbgBml/hO70cSDdmyw3YE9g5eH3wdYs2FCTzexRF3kNAVHNUa8svwIDAQABoAAwDQYJKoZI" +
            "hvcNAQEFBQADgYEAm6uRSyEmyCcs652Ttg2npm6JZPFT2qwSl4dviyIKJbn6j+meCzvn2TMP10d8" +
            "7Ak5sv5NJew1XGkM4mGpF9cfcVshxLVlW+cgq0749fWbyS8KlgQP/ANh3DkLl8k5E+3Wnbi0JjCV" +
            "Xe1s44+K2solX8jOtryoR4TMJ6p9HpsuO68=").getBytes());
    static byte[] iep10 = Base64.decode(("MIICnTCCAgYCAQAwGzEZMBcGA1UEAxMQNkFFSzM0N2Z3OHZXRTQyNDCBnzANBgkq" +
            "hkiG9w0BAQEFAAOBjQAwgYkCgYEAukW70HN9bt5x2AiSZm7y8GXQuyp1jN2OIvqU" +
            "sr0dzLIOFt1H8GPJkL80wx3tLDj3xJfWJdww3TqExsxMSP+qScoYKIOeNBb/2OMW" +
            "p/k3DThCOewPebmt+M08AClq5WofXTG+YxyJgXWbMTNfXKIUyR0Ju4Spmg6Y4eJm" +
            "GXTG7ZUCAwEAAaCCAUAwGgYKKwYBBAGCNw0CAzEMFgo1LjAuMjE5NS4yMCAGCisG" +
            "AQQBgjcCAQ4xEjAQMA4GA1UdDwEB/wQEAwIE8DCB/wYKKwYBBAGCNw0CAjGB8DCB" +
            "7QIBAR5cAE0AaQBjAHIAbwBzAG8AZgB0ACAARQBuAGgAYQBuAGMAZQBkACAAQwBy" +
            "AHkAcAB0AG8AZwByAGEAcABoAGkAYwAgAFAAcgBvAHYAaQBkAGUAcgAgAHYAMQAu" +
            "ADADgYkAjuYPzZPpbLgCWYnXoNeX2gS6nuI4osrWHlQQKcS67VJclhELlnT3hBb9" +
            "Blr7I0BsJ/lguZvZFTZnC1bMeNULRg17bhExTg+nUovzPcJhMvG7G3DR17PrJ7V+" +
            "egHAsQV4dQC2hOGGhOnv88JhP9Pwpso3t2tqJROa5ZNRRSJSkw8AAAAAAAAAADAN" +
            "BgkqhkiG9w0BAQQFAAOBgQCL5k4bJt265j63qB/9GoQb1XFOPSar1BDFi+veCPA2" +
            "GJ/vRXt77Vcr4inx9M51iy87FNcGGsmyesBoDg73p06UxpIDhkL/WpPwZAfQhWGe" +
            "o/gWydmP/hl3uEfE0E4WG02UXtNwn3ziIiJM2pBCGQQIN2rFggyD+aTxwAwOU7Z2" + "fw==").getBytes());
    static byte[] openscep = Base64.decode(("MIIGqwYJKoZIhvcNAQcCoIIGnDCCBpgCAQExDjAMBggqhkiG9w0CBQUAMIICuwYJ" +
            "KoZIhvcNAQcBoIICrASCAqgwggKkBgkqhkiG9w0BBwOgggKVMIICkQIBADGB1TCB" +
            "0gIBADA7MC8xDzANBgNVBAMTBlRlc3RDQTEPMA0GA1UEChMGQW5hVG9tMQswCQYD" +
            "VQQGEwJTRQIIbzEhUVZYO3gwDQYJKoZIhvcNAQEBBQAEgYCksIoSXYsCQPot2DDW" +
            "dexdFqLj1Fuz3xSpu/rLozXKxEY0n0W0JXRR9OxxuyqNw9cLZhiyWkNsJGbP/rEz" +
            "yrXe9NXuLK5U8+qqE8OhnY9BhCxjeUJSLni6oCSi7YzwOqdg2KmifJrQQI/jZIiC" +
            "tSISAtE6qi6DKQwLCkQLmokLrjCCAbIGCSqGSIb3DQEHATARBgUrDgMCBwQILYvZ" +
            "rBWuC02AggGQW9o5MB/7LN4o9G4ZD1l2mHzS+g+Y/dT2qD/qIaQi1Mamv2oKx9eO" +
            "uFtaGkBBGWZlIKg4mm/DFtvXqW8Y5ijAiQVHHPuRKNyIV6WVuFjNjhNlM+DWLJR+" +
            "rpHEhvB6XeDo/pd+TyOKFcxedMPTD7U+j46yd46vKdmoKAiIF21R888uVSz3GDts" +
            "NlqgvZ7VlaI++Tj7aPdOI7JTdQXZk2FWF7Ql0LBIPwk9keffptF5if5Y+aHqB0a2" +
            "uQj1aE8Em15VG8p8MmLJOX0OA1aeqfxR0wk343r44UebliY2DE8cEnym/fmya30/" +
            "7WYzJ7erWofO2ukg1yc93wUpyIKxt2RGIy5geqQCjCYSSGgaNFafEV2pnOVSx+7N" +
            "9z/ICNQfDBD6b83MO7yPHC1cXcdREKHHeqaKyQLiVRk9+R/3D4vEZt682GRaUKOY" +
            "PQXK1Be2nyZoo4gZs62nZVAliJ+chFkEUog9k9OsIvZRG7X+VEjVYBqxlE1S3ikt" +
            "igFXiuLC/LDCi3IgVwQjfNx1/mhxsO7GSaCCAfswggH3MIIBYKADAgEDAiA4OEUy" +
            "REVFNDcwNjhCQjM3RjE5QkE2NDdCRjAyRkQwRjANBgkqhkiG9w0BAQQFADAyMQsw" +
            "CQYDVQQGEwJTZTERMA8GA1UEChMIUHJpbWVLZXkxEDAOBgNVBAMTB1RvbWFzIEcw" +
            "HhcNMDMwNjAxMDgzNDQyWhcNMDMwNzAxMDgzNDQyWjAyMQswCQYDVQQGEwJTZTER" +
            "MA8GA1UEChMIUHJpbWVLZXkxEDAOBgNVBAMTB1RvbWFzIEcwgZ8wDQYJKoZIhvcN" +
            "AQEBBQADgY0AMIGJAoGBAOu47fpIQfzfSnEBTG2WJpKZz1891YLNulc7XgMk8hl3" +
            "nVC4m34SaR7eXR3nCsorYEpPPmL3affaPFsBnNBQNoZLxKmQ1RKiDyu8dj90AKCP" +
            "CFlIM2aJbKMiQad+dt45qse6k0yTrY3Yx0hMH76tRkDif4DjM5JUvdf4d/zlYcCz" +
            "AgMBAAEwDQYJKoZIhvcNAQEEBQADgYEAGNoWI02kXNEA5sPHb3KEY8QZoYM5Kha1" +
            "JA7HLmlXKy6geeJmk329CUnvF0Cr7zxbMkFRdUDUtR8omDDnGlBSOCkV6LLYH939" +
            "Z8iysfaxigZkxUqUYGLtYHhsEjVgcpfKZVxTz0E2ocR2P+IuU04Duel/gU4My6Qv" +
            "LDpwo1CQC10xggHDMIIBvwIBATBWMDIxCzAJBgNVBAYTAlNlMREwDwYDVQQKEwhQ" +
            "cmltZUtleTEQMA4GA1UEAxMHVG9tYXMgRwIgODhFMkRFRTQ3MDY4QkIzN0YxOUJB" +
            "NjQ3QkYwMkZEMEYwDAYIKoZIhvcNAgUFAKCBwTASBgpghkgBhvhFAQkCMQQTAjE5" +
            "MBgGCSqGSIb3DQEJAzELBgkqhkiG9w0BBwEwHAYJKoZIhvcNAQkFMQ8XDTAzMDYw" +
            "MTA4MzQ0MlowHwYJKoZIhvcNAQkEMRIEEBqGJFo7n4B8sFBCi54PckIwIAYKYIZI" +
            "AYb4RQEJBTESBBA77Owxh2rbflhXsDYw3xsLMDAGCmCGSAGG+EUBCQcxIhMgODhF" +
            "MkRFRTQ3MDY4QkIzN0YxOUJBNjQ3QkYwMkZEMEYwDQYJKoZIhvcNAQEBBQAEgYB4" +
            "BPcw4NPIt4nMOFKSGg5oM1nGDPGFN7eorZV+/2uWiQfdtK4B4lzCTuNxWRT853dW" +
            "dRDzXBCGEArlG8ef+vDD/HP9SX3MQ0NJWym48VI9bTpP/mJlUKSsfgDYHohvUlVI" +
            "E5QFC6ILVLUmuWPGchUEAb8t30DDnmeXs8QxdqHfbQ==").getBytes());
    static byte[] pixscep = Base64.decode(("MIAGCSqGSIb3DQEHAqCAMIACAQExDjAMBggqhkiG9w0CBQUAMIAGCSqGSIb3DQEH" +
            "AaCAJIAEggOZMIAGCSqGSIb3DQEHA6CAMIACAQAxgDCCAYMCAQAwazBfMSYwJAYJ" +
            "KoZIhvcNAQkBFhdwb3N0bWFzdGVyQHRkY29ubGluZS5kazEPMA0GA1UEAxMGdGRj" +
            "b2NhMRYwFAYDVQQLEw1PbmxpbmUgUHJpdmF0MQwwCgYDVQQKEwNUREMCCD7fK8fm" +
            "K2DpMA0GCSqGSIb3DQEBAQUABIIBABlqDG3Jx7NJ4VLTb38JxUB3hhpRx+TUMmjZ" +
            "PG64gFDcK8aNSW5O8dIG09GcfD1dyaW1lwVRUpcFlraEWWCV3xjpM2wPARZ169dL" +
            "j1K/Y+s4mZsqppm45d0KT7jQ/e0oBJUukJq67rtc90Qyst4W9eGYERulxiQTOILD" +
            "x43IpHOGlr9ta1oTsxVKvB6mxdGSSdlkem6eozEkKe2cUbDPGVvc4/O5F/zK7jrb" +
            "L6woflqhwOc+faEOnuCETlr9MUvyN0XdMUbp6Rc3YQkfZj0otgPQ4GjCKfwtui2R" +
            "LT4eD4m0TOuwFlsV+E0YJJFMLxrhjowIeZap2HKSpZ6Qmhmv0EQAADCABgkqhkiG" +
            "9w0BBwEwEQYFKw4DAgcECB3fKGT54qD/oIAEggHAA1e6sQ+qC5YZ0BsYz2tKpWHk" +
            "+dTCsKvRByxTDTjz5xm21pVvyd5iM/k08S674uuxW+V91Rt4OJ13YOUuZ27dyfz8" +
            "rf/1kRFI0Y8He8Ye5mwJ5beAHiv4gb2hlci8doPp8FkeerB/HM1JxBV0/GQWugyo" +
            "b6Z3clqXa3WkTI1Pa8dIKql4a1QBi+iXiz+Tg8BR+yUIKHdmfc6HISOqGGmthB5+" +
            "x7uOjqK4unI3LILauAfRbeQQalFn/PUxQuxNJWf2A0lOwxxtIEVBX0XwxKkejuX6" +
            "CVxDKaTkt90g3zeZLYvsEdLieGPnw4NhC91/+NycQdjoOpEQCGHjgUGRwX2v0CKg" +
            "hMSpFLTrsWB/o5X6G/Z5mdAKGKVIoBIj15BxesJAAx8KI3Rni8opie/EWjiXOEwb" +
            "d6+Ie80jxxyccsXgpBLNnx8EUmQzU1RwWOq3jmzJCtDzKHljaqCxTi6uyFAbsCF9" +
            "Okl2Qj1qc+rX9ah+F53BDHfXuV3WpCFBSCxi7/G48LXc7Lna6vcEY9eYR3alpUDW" +
            "ciV7k92bQgBwwHuaXh4brb0MytCcgVUXEwHL6+GI9CHsMT6JlMezIsCTlqZlIwQI" +
            "57+qPArzctYAAAAAAAAAAAAAAAAAAAAAoIAwggI0MIIBnQIgNzBiNDg2ZmZmMmYy" +
            "ZWIxYzAzZmRiYjljOWFjNmE2MmEwDQYJKoZIhvcNAQEEBQAwUzFRMA8GA1UEBRMI" +
            "MzAxYjJkODkwGwYDVQQDExRodWwudGVrbmV0Lm9wYXNpYS5kazAhBgkqhkiG9w0B" +
            "CQIWFGh1bC50ZWtuZXQub3Bhc2lhLmRrMB4XDTA0MDMxMTA5MTQ1NFoXDTE0MDMw" +
            "OTA5MTQ1NFowUzFRMA8GA1UEBRMIMzAxYjJkODkwGwYDVQQDExRodWwudGVrbmV0" +
            "Lm9wYXNpYS5kazAhBgkqhkiG9w0BCQIWFGh1bC50ZWtuZXQub3Bhc2lhLmRrMIGf" +
            "MA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC/AtkNDP14TWSkJqDCtPu3T3JeqvyY" +
            "3Jqww+3ZNfUbs9njCycuiajHbbDVKEyffXCOtzE7GtkcSXJZKntbTICT7N4M+eot" +
            "EHOtri3l9DkvXWqdvHw21d4i83q+NKPkaXmo6h5yIwmtDQEVIccLDwQydMb0XDgK" +
            "PjCOm9MC33Pm2wIDAQABMA0GCSqGSIb3DQEBBAUAA4GBAAzXcoUUTmNV4UyxZ/01" +
            "FHafeqQJmmq66+TrIXUAcwfWyvtgIRGDb/kif2NsjDtvFvnlXUiM57K/I+/qVqQm" +
            "HF1Thx1L/sbvNwWqYZxyWJPm1TQaw2zvAu0Hpc53/T49dH8LgYYrwEOXRTyW5YE1" +
            "9fMRCmp78VeN/nJyoOGcJKigAAAxgDCCAcICAQEwdzBTMVEwDwYDVQQFEwgzMDFi" +
            "MmQ4OTAbBgNVBAMTFGh1bC50ZWtuZXQub3Bhc2lhLmRrMCEGCSqGSIb3DQEJAhYU" +
            "aHVsLnRla25ldC5vcGFzaWEuZGsCIDcwYjQ4NmZmZjJmMmViMWMwM2ZkYmI5Yzlh" +
            "YzZhNjJhMAwGCCqGSIb3DQIFBQCggaMwEgYKYIZIAYb4RQEJAjEEEwIxOTAYBgkq" +
            "hkiG9w0BCQMxCwYJKoZIhvcNAQcBMB8GCSqGSIb3DQEJBDESBBCQVbLp6teJEWNq" +
            "nBD/Kr1GMCAGCmCGSAGG+EUBCQUxEgQQbpDDbO95LE1U7ZbbEe2p8TAwBgpghkgB" +
            "hvhFAQkHMSITIDcwYjQ4NmZmZjJmMmViMWMwM2ZkYmI5YzlhYzZhNjJhMA0GCSqG" +
            "SIb3DQEBAQUABIGAfiGzBNxJiy4XI3DG13Osso4qV+7rvwg+CVbe0gqt01s1kd5A" +
            "LxwTYVhXXKG1spaBnebu+T9xZDZqvF9eY1ANJVNSIpNygKmJdhjsJivKFkD9Jz2y" +
            "F/BYZzv618HlvZQj9Sbv7PaODRU4xqGVifa6LllK/572uQdUQj3FTkssqFQAAAAA" +
            "AAAAAA==").getBytes());
    static byte[] p12 = Base64.decode(("MIACAQMwgAYJKoZIhvcNAQcBoIAkgASCAy4wgDCABgkqhkiG9w0BBwGggCSABIID" +
            "FjCCAxIwggMOBgsqhkiG9w0BDAoBAqCCArEwggKtMCcGCiqGSIb3DQEMAQMwGQQU" +
            "VnhToLFkCvaqCu8OkXi6Jljy/Y8CAWQEggKAv4LDUWwUi7eZKiCoYsqevVUAEOwE" +
            "dy8xOmtyjzEPO0W8pltybVk2nfEaS6CrPQLvi1aAQD/5NjkO0agfgwS8gOZPIRaX" +
            "HzETkxsR6tNd1eP5jGXOYfxhQGGL3laVvvGhrNFbx7AW/ugMgBPMj+OWRkuSMVBY" +
            "uZgmBX3CM14UFl1X4jVG/nX/eS1LKIdDI2hcebWvkpuOWR46LxH1yXgSqW0RYjCV" +
            "ZhNvsQtvQGfgMBgGOhjec+p6xMiw8OXSR6kfDVHJyJfFwqz2DTz1zIfi28WPVCSv" +
            "2TmMPvPFGjbE5bo8PlVh5Gu5sX9DzQQ41Vio/c1dxwBDe4CgAYuDfI0Fu0ZVSMZM" +
            "TSDt7lC4t/YoxqFCxSBn9pmYOCLBiBmQgtzJZfQYrMJSdaBqXWNx5vUibd+K/tzL" +
            "Hfik1SmH+MY8bjBC/PSlCz8cbAwAdqGKCN5kjT+RcPM5oRIAc4isXs+epApzf4jF" +
            "AKqgNFnHKfgTEabVT9E/QUEwS7PfDi2jzID/8a3LUQvjp95B9kD6DJH4nlIZVT+2" +
            "aHzOooZ2K54pqq2OlS6yeYKRo2GcGdVcm13zw+wBnw+2Zz34zrzd1Uq9wGuoduNF" +
            "VJNKqNkLvva94InFAaiPbHgAkH179pVF8oTeFOh0NXBTj4mZQgpgYPO1ASMZEvY7" +
            "5nC/Uf+6kyr92qc0s4GpAV1Sm2lsSyBdINAxSnzW2XSJ389RztAN5H6ycUcJBbaG" +
            "N9DfBSxK8kkFRW7b8dx7PXd2ofe9U/pIJgRlscPSC30cRRp4jT2JXvpW+D3EocI8" +
            "uUEzRSliEpec1zn2SrPUKCCQVc6BoBHsN62/I1LtM2+Wybx5fyRGsw7i4zFKMCMG" +
            "CSqGSIb3DQEJFDEWHhQAcAByAGkAdgBhAHQAZQBLAGUAeTAjBgkqhkiG9w0BCRUx" +
            "FgQUY3v0dqhUJI6ldKV3RKb0Xg9XklEABAEABAEABAEABAEABIIDAgAwgAYJKoZI" +
            "hvcNAQcGoIAwgAIBADCABgkqhkiG9w0BBwEwJwYKKoZIhvcNAQwBBjAZBBTDw4v0" +
            "l2xpgaM/AkWLRwcyAaI5lQIBZKCABIICsEY/4hTSq1sKzRlg+oP1Cu/fGipwALox" +
            "HFj5wvC+hN78ZMjFYAK3b61ft0hUKeqtmzDDzgbMn9qEjsV3WERKID1AqNy18a2j" +
            "i7MuR2sidbz7H7pOr2QrKajmiFf4IKiXXiqrx9qnF40l3HzUGaiGw36BvFZ1lXkJ" +
            "HRA97mTcuEczZXkp5N+U9e7sztQE0b7MYcGPYi01CpJzYSRryU4BRG4a0vRhEJFp" +
            "mHu+mfpaBux93HvOeOFD/bb59EUuoGgog2dYUFRL4ZTH99I0MpcHbRp/wIeNdMpJ" +
            "KOjzrw1OHzkkqLOTC/m4nI+da3OXShPdByTHHdZ29fNYCVxdOgqUtym1PP7cHsUn" +
            "Y5PThfXM5ZXV0G7pvC0zVz+qTS6G+Xg8bu91g07jh5HOSHOUfa/XMhhLZFUgYr9r" +
            "7ZId98C+lg7atW3LjhJ9FPawogXpDXp+wo/NNp2Lq7KHyevJSfwkrLMOeETo9tMY" +
            "NUv/zPPGpiVUZX75zNkOx+YlL0dUJ4VcorXpDRs/OwM7CEGJSuGytXLz7eNEYVak" +
            "iLhlb8vwJrkrlhFDd7vu9G5UAOb4Sp7IWwEooO/yo6/rDusXoT6+jFJ6bt27lEFj" +
            "3PUYSijBCbVtn7Wqd6sKWqeCd10RZjz5AME3xiOcKWPtIYfFsztJmvjuRBxM5gi0" +
            "QOwqdNd63apOv1I/nzPDgBYlIhH7kn+5jMb0RHJarMWuSTJQDpQYctRnpvPSM1HE" +
            "srjxQ8n4Ukbg+XoUHTS4VPuxGf0NYakW5CZDJeKaJ+a/R3oU37esYOikmMJPnSUa" +
            "PKZ1XJeHqK7kCrnbiw/WOYowaUw+BuIjpqSwgNyWktKLXWKreMtKjGtbxZ01BSsM" +
            "VhyB+EXgjzqMBGGnCxbJ0aA4AoSBS73XvqlB+S8FUbmi7XfzPvKM4XMABAEABAEA" +
            "BAEABAEABAEABAEABAEABAEABAEABAEABAEAAAAAAAAAMDwwITAJBgUrDgMCGgUA" +
            "BBQ/qUCCCV8/5FhF5438mA7FYj0eKQQUC1bpAMlQMV4fwFz/nNVuiJUqmkYCAWQA" + "AA==").getBytes());
    private PrivateKey privateKey = null;
    private X509Certificate caCert = null;
    private static Logger log = Logger.getLogger(TestMessages.class);

    /**
     * Creates a new TestMessages object.
     *
     * @param name name
     */
    public TestMessages(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        log.debug(">setUp()");

        // Install BouncyCastle provider
        CertTools.installBCProvider();

        KeyStore keyStore = KeyStore.getInstance("PKCS12", "BC");
        InputStream is = new ByteArrayInputStream(p12);
        String keyStorePass = "foo123";
        keyStore.load(is, keyStorePass.toCharArray());

        String privateKeyAlias = "privateKey";
        char[] pkPass = null;
        privateKey = (PrivateKey) keyStore.getKey(privateKeyAlias, pkPass);

        if (privateKey == null) {
            log.error("Cannot load key with alias '" + privateKeyAlias + "' from keystore.");
            throw new Exception("Cannot load key with alias '" + privateKeyAlias +
                    "' from keystore.");
        }

        Certificate[] certchain = KeyTools.getCertChain(keyStore, privateKeyAlias);
        caCert = (X509Certificate) certchain[0];

        //System.out.println(caCert.toString());
        log.debug("<setUp()");
    }

    protected void tearDown() throws Exception {
    }

    /** Tests scep message from OpenScep
     * @throws Exception error
     */
    public void test01TestOpenScep() throws Exception {
        log.debug(">test01TestOpenScep()");
        ScepRequestMessage msg = new ScepRequestMessage(openscep);
        // You should be able to get issuer DN before anything else
        String issuerdn = msg.getIssuerDN();
        log.debug("IssuerDN: " + issuerdn);
        assertEquals("CN=TestCA,O=AnaTom,C=SE", issuerdn);
        if (msg.requireKeyInfo()) {
            msg.setKeyInfo(caCert, privateKey);
        }
        boolean ret = msg.verify();
        String dn = msg.getRequestDN();
        log.debug("DN: " + dn);
        assertEquals("C=Se,O=PrimeKey,CN=Tomas G", dn);
        String pwd = msg.getPassword();
        log.debug("Pwd: " + pwd);
        assertEquals("foo123", pwd);
        assertTrue("Failed to verify SCEP message from OpenSCEP.", ret);
        log.debug("<test01TestOpenScep()");
    }

    /** Tests scep message from Cisco PIX
     * @throws Exception error
     */
    public void test02TestPixScep() throws Exception {
        log.debug(">test02TestPixScep()");
        ScepRequestMessage msg = new ScepRequestMessage(pixscep);
        // You should be able to get issuer DN before anything else
        String issuerdn = msg.getIssuerDN();
        log.debug("IssuerDN: " + issuerdn);
        assertEquals("E=postmaster@tdconline.dk,CN=tdcoca,OU=Online Privat,O=TDC", issuerdn);
        if (msg.requireKeyInfo()) {
            msg.setKeyInfo(caCert, privateKey);
        }
        boolean ret = msg.verify();
        String dn = msg.getRequestDN();
        log.debug("DN: " + dn);
        assertEquals("C=Se,O=PrimeKey,CN=Tomas G", dn);
        String pwd = msg.getPassword();
        log.debug("Pwd: " + pwd);
        assertEquals("foo123", pwd);
        assertTrue("Failed to verify SCEP message from PIX.", ret);
        log.debug("<test02TestPixScep()");
    }

}
